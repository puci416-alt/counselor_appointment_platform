package com.counselor.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.counselor.common.constant.CommonConstant;
import com.counselor.common.exception.BusinessException;
import com.counselor.common.result.ResultCode;
import com.counselor.order.dto.AppointmentCreatedMessage;
import com.counselor.order.entity.AppointmentOrder;
import com.counselor.order.entity.CounselorSchedule;
import com.counselor.order.mapper.AppointmentOrderMapper;
import com.counselor.order.mapper.CounselorScheduleMapper;
import com.counselor.order.mq.AppointmentMessageProducer;
import com.counselor.order.query.AppointmentCreateCommand;
import com.counselor.order.service.AppointmentService;
import com.counselor.order.service.ScheduleService;
import cn.hutool.core.util.IdUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Resource
    private AppointmentOrderMapper orderMapper;

    @Resource
    private CounselorScheduleMapper scheduleMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private DefaultRedisScript<Long> stockDeductScript;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private AppointmentMessageProducer messageProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentOrder create(AppointmentCreateCommand command, Long userId) {
        Long scheduleId = command.getScheduleId();

        // ---------- 0. 幂等校验（必须在最前面！）----------
        LambdaQueryWrapper<AppointmentOrder> idempotentWrapper = new LambdaQueryWrapper<>();
        idempotentWrapper.eq(AppointmentOrder::getUserId, userId)
                .eq(AppointmentOrder::getScheduleId, scheduleId)
                .eq(AppointmentOrder::getIsDeleted, 0)
                .in(AppointmentOrder::getStatus,
                        CommonConstant.ORDER_PENDING,
                        CommonConstant.ORDER_CONFIRMED);
        if (orderMapper.selectCount(idempotentWrapper) > 0) {
            throw new BusinessException("您已预约该时段，请勿重复提交");
        }

        // ---------- 1. 校验时段存在 ----------
        CounselorSchedule schedule = scheduleService.getById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }

        // ---------- 2. Redis Lua 原子扣库存 ----------
        String stockKey = ScheduleServiceImpl.STOCK_KEY_PREFIX + scheduleId;
        Long result = redisTemplate.execute(
                stockDeductScript,
                Collections.singletonList(stockKey),
                1
        );

        if (result == null || result == -2) {
            // 库存 key 不存在，说明 Redis 没预热，回退到数据库校验
            log.warn("Redis 库存 key 不存在，回退数据库校验: {}", stockKey);
            return createWithDbLock(command, userId, schedule);
        }
        if (result == -1) {
            throw new BusinessException(ResultCode.SCHEDULE_ALREADY_BOOKED);
        }
        log.info("Lua 扣库存成功，时段 {} 剩余库存: {}", scheduleId, result);

        // ---------- 3. 更新时段状态为已约 ----------
        int updated = scheduleMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CounselorSchedule>()
                        .eq(CounselorSchedule::getId, scheduleId)
                        .eq(CounselorSchedule::getStatus, CommonConstant.SCHEDULE_AVAILABLE)
                        .set(CounselorSchedule::getStatus, CommonConstant.SCHEDULE_BOOKED));
        if (updated == 0) {
            // 数据库更新失败，回滚 Redis 库存
            redisTemplate.opsForValue().increment(stockKey, 1);
            throw new BusinessException(ResultCode.SCHEDULE_ALREADY_BOOKED);
        }

        // ---------- 4. 写订单 ----------
        AppointmentOrder order = new AppointmentOrder();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setCounselorId(command.getCounselorId());
        order.setScheduleId(scheduleId);
        order.setAppointmentTime(
                LocalDateTime.of(schedule.getScheduleDate(), schedule.getStartTime())
        );
        order.setStatus(CommonConstant.ORDER_PENDING);
        order.setRemark(command.getRemark());
        orderMapper.insert(order);

        // ---------- 4.5 发送 Kafka 异步通知（新增） ----------
        try {
            AppointmentCreatedMessage message = new AppointmentCreatedMessage(
                    order.getId(),
                    order.getOrderNo(),
                    userId,
                    command.getCounselorId(),
                    scheduleId,
                    order.getAppointmentTime(),
                    LocalDateTime.now()
            );
            messageProducer.sendAppointmentCreated(message);
        } catch (Exception e) {
            // Kafka 发送失败不影响主流程
            log.error("发送 Kafka 通知失败，不影响预约: orderId={}", order.getId(), e);
        }

        // ---------- 5. 回写 orderId 到时段 ----------
        scheduleMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CounselorSchedule>()
                        .eq(CounselorSchedule::getId, scheduleId)
                        .set(CounselorSchedule::getOrderId, order.getId())
                        .set(CounselorSchedule::getStatus, CommonConstant.SCHEDULE_BOOKED));

        log.info("预约成功: orderNo={}, userId={}, scheduleId={}",
                order.getOrderNo(), userId, scheduleId);
        return order;
    }

    /**
     * 降级方案：Redis 未预热时，用数据库乐观锁扣减
     */
    private AppointmentOrder createWithDbLock(AppointmentCreateCommand command,
                                              Long userId,
                                              CounselorSchedule schedule) {
        int updated = scheduleMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CounselorSchedule>()
                        .eq(CounselorSchedule::getId, command.getScheduleId())
                        .eq(CounselorSchedule::getStatus, CommonConstant.SCHEDULE_AVAILABLE)
                        .set(CounselorSchedule::getStatus, CommonConstant.SCHEDULE_BOOKED));
        if (updated == 0) {
            throw new BusinessException(ResultCode.SCHEDULE_ALREADY_BOOKED);
        }

        AppointmentOrder order = new AppointmentOrder();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setCounselorId(command.getCounselorId());
        order.setScheduleId(command.getScheduleId());
        order.setAppointmentTime(
                LocalDateTime.of(schedule.getScheduleDate(), schedule.getStartTime())
        );
        order.setStatus(CommonConstant.ORDER_PENDING);
        order.setRemark(command.getRemark());
        orderMapper.insert(order);

        // ---------- 发送 Kafka 异步通知 ----------
        try {
            AppointmentCreatedMessage message = new AppointmentCreatedMessage(
                    order.getId(),
                    order.getOrderNo(),
                    userId,
                    command.getCounselorId(),
                    command.getScheduleId(),
                    order.getAppointmentTime(),
                    LocalDateTime.now()
            );
            messageProducer.sendAppointmentCreated(message);
        } catch (Exception e) {
            // Kafka 发送失败不能影响主流程（预约已经成功了）
            log.error("发送 Kafka 通知失败，不影响预约: orderId={}", order.getId(), e);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long orderId, Long userId) {
        log.info("【取消】开始, orderId={}, userId={}", orderId, userId);

        AppointmentOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!CommonConstant.ORDER_PENDING.equals(order.getStatus())
                && !CommonConstant.ORDER_CONFIRMED.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CANCEL);
        }

        // 1. 更新订单状态
        orderMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<AppointmentOrder>()
                        .eq(AppointmentOrder::getId, orderId)
                        .set(AppointmentOrder::getStatus, CommonConstant.ORDER_CANCELLED)
                        .set(AppointmentOrder::getCancelReason, "用户主动取消")
                        .set(AppointmentOrder::getIsDeleted, 1)   // 关键！显式 set
        );
        log.info("【取消】订单状态已更新");

        // 2. 释放时段
        scheduleMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CounselorSchedule>()
                        .eq(CounselorSchedule::getId, order.getScheduleId())
                        .eq(CounselorSchedule::getOrderId, orderId)
                        .set(CounselorSchedule::getStatus, CommonConstant.SCHEDULE_AVAILABLE)
                        .set(CounselorSchedule::getOrderId, null));

        // 3. 恢复 Redis 库存
        redisTemplate.opsForValue().set("counselor:schedule:stock:" + order.getScheduleId(), 1);
    }

    @Override
    public IPage<AppointmentOrder> pageByUser(Long userId, int page, int size) {
        Page<AppointmentOrder> p = new Page<>(page, size);
        LambdaQueryWrapper<AppointmentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppointmentOrder::getUserId, userId)
                .orderByDesc(AppointmentOrder::getCreateTime);
        return orderMapper.selectPage(p, wrapper);
    }
}
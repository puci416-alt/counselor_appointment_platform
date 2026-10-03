package com.counselor.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.counselor.common.constant.CommonConstant;
import com.counselor.common.exception.BusinessException;
import com.counselor.common.result.ResultCode;
import com.counselor.order.entity.CounselorSchedule;
import com.counselor.order.mapper.CounselorScheduleMapper;
import com.counselor.order.service.ScheduleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    /** 库存 key 前缀 */
    public static final String STOCK_KEY_PREFIX = "counselor:schedule:stock:";

    @Resource
    private CounselorScheduleMapper scheduleMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public List<CounselorSchedule> listAvailableByCounselor(Long counselorId) {
        LambdaQueryWrapper<CounselorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CounselorSchedule::getCounselorId, counselorId)
                .ge(CounselorSchedule::getScheduleDate, java.time.LocalDate.now())
                .orderByAsc(CounselorSchedule::getScheduleDate)
                .orderByAsc(CounselorSchedule::getStartTime);
        return scheduleMapper.selectList(wrapper);
    }

    @Override
    public void warmUpStock(Long scheduleId) {
        // 加分布式锁，防止多实例并发预热同一时段
        RLock lock = redissonClient.getLock("lock:schedule:warm-up:" + scheduleId);
        try {
            // 最多等 1 秒，锁持有 5 秒
            if (!lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                log.warn("预热库存获取锁失败，跳过: scheduleId={}", scheduleId);
                return;
            }
            try {
                CounselorSchedule schedule = scheduleMapper.selectById(scheduleId);
                if (schedule == null) {
                    return;
                }
                String key = STOCK_KEY_PREFIX + scheduleId;
                // 可约 → 库存 1；已约/不可约 → 库存 0
                int stock = CommonConstant.SCHEDULE_AVAILABLE.equals(schedule.getStatus()) ? 1 : 0;
                redisTemplate.opsForValue().set(key, stock);
                log.info("预热库存: {} = {}", key, stock);
            } finally {
                // 只释放自己持有的锁
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("预热库存被中断: scheduleId={}", scheduleId, e);
        }
    }

    @Override
    public CounselorSchedule getById(Long id) {
        CounselorSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }
        return schedule;
    }

    @Override
    public List<CounselorSchedule> list(LambdaQueryWrapper<CounselorSchedule> eq) {
        return List.of();
    }
}

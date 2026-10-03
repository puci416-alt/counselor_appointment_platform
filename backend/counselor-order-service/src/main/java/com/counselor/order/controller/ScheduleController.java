package com.counselor.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.counselor.common.result.Result;
import com.counselor.order.entity.AppointmentOrder;
import com.counselor.order.entity.CounselorSchedule;
import com.counselor.order.mapper.AppointmentOrderMapper;
import com.counselor.order.mapper.CounselorScheduleMapper;
import com.counselor.order.service.ScheduleService;
import com.counselor.order.task.ScheduleGenerateTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private CounselorScheduleMapper scheduleMapper;

    @Resource
    private AppointmentOrderMapper orderMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ScheduleGenerateTask scheduleGenerateTask;

    /** 查询某咨询师的可约时段 */
    @GetMapping("/counselor/{counselorId}")
    public Result<List<CounselorSchedule>> listByCounselor(@PathVariable Long counselorId) {
        return Result.success(scheduleService.listAvailableByCounselor(counselorId));
    }

    /** 批量预热所有可约时段 */
    @PostMapping("/warm-up/all")
    public Result<Integer> warmUpAll() {
        List<CounselorSchedule> available = scheduleService.list(
                new LambdaQueryWrapper<CounselorSchedule>()
                        .eq(CounselorSchedule::getStatus, 0)
        );
        for (CounselorSchedule s : available) {
            scheduleService.warmUpStock(s.getId());
        }
        return Result.success(available.size());
    }

    /** 手动生成未来 7 天时段（调试用） */
    @GetMapping("/generate-now")
    public Result<Integer> generateNow() {
        int count = scheduleGenerateTask.generateSchedules();
        return Result.success(count);
    }
}
package com.counselor.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.counselor.order.entity.CounselorSchedule;

import java.util.List;

public interface ScheduleService {

    /** 查询某咨询师的可约时段 */
    List<CounselorSchedule> listAvailableByCounselor(Long counselorId);

    /** 预热库存到 Redis */
    void warmUpStock(Long scheduleId);

    /** 查询时段详情 */
    CounselorSchedule getById(Long id);

    List<CounselorSchedule> list(LambdaQueryWrapper<CounselorSchedule> eq);
}

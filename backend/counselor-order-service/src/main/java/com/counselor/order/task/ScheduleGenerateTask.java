package com.counselor.order.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.counselor.order.entity.CounselorSchedule;
import com.counselor.order.mapper.CounselorScheduleMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ScheduleGenerateTask {

    @Resource
    private CounselorScheduleMapper scheduleMapper;

    /** 每天上班时段 */
    private static final LocalTime[][] SLOTS = {
            { LocalTime.of(9, 0),  LocalTime.of(10, 0) },
            { LocalTime.of(10, 0), LocalTime.of(11, 0) },
            { LocalTime.of(14, 0), LocalTime.of(15, 0) },
            { LocalTime.of(15, 0), LocalTime.of(16, 0) },
            { LocalTime.of(16, 0), LocalTime.of(17, 0) }
    };

    /** 咨询师 ID 列表 */
    private static final Long[] COUNSELOR_IDS = { 1L, 2L, 3L };

    /** 核心逻辑：可被定时任务和手动接口调用 */
    public int generateSchedules() {
        log.info("开始生成未来 7 天时段");

        List<CounselorSchedule> toInsert = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Long counselorId : COUNSELOR_IDS) {
            for (int day = 1; day <= 7; day++) {
                LocalDate date = today.plusDays(day);
                for (LocalTime[] slot : SLOTS) {
                    Long count = scheduleMapper.selectCount(
                            new LambdaQueryWrapper<CounselorSchedule>()
                                    .eq(CounselorSchedule::getCounselorId, counselorId)
                                    .eq(CounselorSchedule::getScheduleDate, date)
                                    .eq(CounselorSchedule::getStartTime, slot[0])
                    );
                    if (count > 0) continue;

                    CounselorSchedule s = new CounselorSchedule();
                    s.setCounselorId(counselorId);
                    s.setScheduleDate(date);
                    s.setStartTime(slot[0]);
                    s.setEndTime(slot[1]);
                    s.setStatus(0);
                    toInsert.add(s);
                }
            }
        }

        for (CounselorSchedule s : toInsert) {
            scheduleMapper.insert(s);
        }

        log.info("生成时段完成，共 {} 条", toInsert.size());
        return toInsert.size();
    }


}
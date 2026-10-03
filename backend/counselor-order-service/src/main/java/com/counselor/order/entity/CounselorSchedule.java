package com.counselor.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("counselor_schedule")
public class CounselorSchedule implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long counselorId;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;

    /** 0-可约 1-已约 2-不可约 */
    private Integer status;

    private Long orderId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}

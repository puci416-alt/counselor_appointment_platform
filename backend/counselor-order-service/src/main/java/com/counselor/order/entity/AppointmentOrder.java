package com.counselor.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("appointment_order")
public class AppointmentOrder implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long userId;
    private Long counselorId;
    private Long scheduleId;
    private LocalDateTime appointmentTime;

    /** 0-待确认 1-已确认 2-已完成 3-已取消 */
    private Integer status;

    private String remark;
    private String cancelReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
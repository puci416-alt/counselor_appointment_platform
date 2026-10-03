package com.counselor.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreatedMessage implements Serializable {

    private Long orderId;
    private String orderNo;
    private Long userId;
    private Long counselorId;
    private Long scheduleId;
    private LocalDateTime appointmentTime;
    private LocalDateTime createdAt;
}
package com.counselor.order.query;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentCreateCommand {

    @NotNull(message = "时段ID不能为空")
    private Long scheduleId;

    @NotNull(message = "咨询师ID不能为空")
    private Long counselorId;

    private String remark;
}

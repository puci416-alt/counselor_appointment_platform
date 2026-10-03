package com.counselor.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.counselor.order.entity.AppointmentOrder;
import com.counselor.order.query.AppointmentCreateCommand;

public interface AppointmentService {

    /** 创建预约 */
    AppointmentOrder create(AppointmentCreateCommand command, Long userId);

    /** 取消预约 */
    void cancel(Long orderId, Long userId);

    /** 当前用户的分页列表 */
    IPage<AppointmentOrder> pageByUser(Long userId, int page, int size);
}

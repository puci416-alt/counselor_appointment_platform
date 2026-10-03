package com.counselor.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.counselor.order.entity.AppointmentOrder;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentOrderMapper extends BaseMapper<AppointmentOrder> {
}

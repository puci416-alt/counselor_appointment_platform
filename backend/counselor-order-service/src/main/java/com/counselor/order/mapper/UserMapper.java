package com.counselor.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.counselor.order.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

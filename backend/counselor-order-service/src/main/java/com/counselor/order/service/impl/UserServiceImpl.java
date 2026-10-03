package com.counselor.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.counselor.common.exception.BusinessException;
import com.counselor.common.result.ResultCode;
import com.counselor.common.util.JwtUtil;
import com.counselor.order.entity.User;
import com.counselor.order.mapper.UserMapper;
import com.counselor.order.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public String login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 实际项目用 BCrypt，这里演示先明文比对
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
}
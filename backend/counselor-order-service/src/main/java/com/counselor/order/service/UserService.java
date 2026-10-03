package com.counselor.order.service;

import com.counselor.order.entity.User;

public interface UserService {

    /** 登录，返回 Token */
    String login(String username, String password);

    /** 根据 id 查用户 */
    User getById(Long id);
}

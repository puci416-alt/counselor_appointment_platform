package com.counselor.order.controller;

import com.counselor.common.context.UserContext;
import com.counselor.common.result.Result;
import com.counselor.order.entity.User;
import com.counselor.order.service.UserService;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /** 登录 */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginCommand cmd) {
        String token = userService.login(cmd.getUsername(), cmd.getPassword());
        return Result.success(token);
    }

    /** 获取当前用户信息 */
    @GetMapping("/info")
    public Result<User> info() {
        Long userId = UserContext.getUserId();
        return Result.success(userService.getById(userId));
    }

    @Data
    public static class LoginCommand {
        private String username;
        private String password;
    }
}
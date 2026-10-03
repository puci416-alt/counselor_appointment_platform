package com.counselor.common.interceptor;

import com.counselor.common.context.UserContext;
import com.counselor.common.result.ResultCode;
import com.counselor.common.exception.BusinessException;
import com.counselor.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("【AuthInterceptor】请求路径: " + request.getRequestURI()
                + "，token: " + request.getHeader("token"));
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null || token.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        try {
            Claims claims = JwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);
            Integer userType = claims.get("userType", Integer.class);
            UserContext.set(userId, username, userType);
            return true;
        } catch (Exception e) {
            log.warn("Token 解析失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
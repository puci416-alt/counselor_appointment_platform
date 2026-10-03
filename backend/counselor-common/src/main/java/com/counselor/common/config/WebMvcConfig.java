package com.counselor.common.config;

import com.counselor.common.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/api/counselor/page",
                        "/api/counselor/*",
                        "/api/schedule/counselor/*",
                        "/error",
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**"
                );
    }
}

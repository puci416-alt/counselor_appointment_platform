package com.counselor.service.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.counselor.service.entity.Counselor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    /**
     * 咨询师列表本地缓存
     * 最大 1000 条，写入后 5 分钟过期
     */
    @Bean
    public Cache<String, Object> counselorCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }
}

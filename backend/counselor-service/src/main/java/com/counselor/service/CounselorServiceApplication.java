package com.counselor.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.counselor")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.counselor")
@MapperScan("com.counselor.service.mapper")
public class CounselorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CounselorServiceApplication.class, args);
    }
}
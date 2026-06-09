package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * 启动后端后
 * 前端访问 http://localhost
 */

@Slf4j
@SpringBootApplication
@EnableTransactionManagement //开启事务管理
@EnableCaching  //开启缓存
@EnableScheduling //spring task 定时任务，开启任务调度
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("SkyTakeOut Server (Spring Boot 4.0.5 & JDK 21) started successfully.");
    }
}
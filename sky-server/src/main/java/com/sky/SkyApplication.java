package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement //开启事务管理
@EnableCaching  //开启缓存
@EnableScheduling //spring task 定时任务，开启任务调度
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        // 在 JDK 25 中，建议使用更现代的日志打印方式，不过 log.info 依然是标准
        log.info("SkyTakeOut Server (Spring Boot 4.0.5 & JDK 25) started successfully.");
    }
}
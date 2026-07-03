package com.mes.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MES 制造执行系统 - 管理后台 启动类
 * 
 * 技术栈：Spring Boot 2.7.18 + Java 8
 *          Spring Data JPA + 多数据源
 *          Redis + JWT + Spring Security
 *          Knife4j (Swagger) + OpenOffice
 *          数据库支持：Oracle / 达梦(DM)
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableAsync
@EnableScheduling
public class MesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesApplication.class, args);
        System.out.println("==========================================");
        System.out.println("  MES 制造执行系统 - 管理后台 启动成功! ");
        System.out.println("  API 文档: http://localhost:8080/doc.html");
        System.out.println("==========================================");
    }
}

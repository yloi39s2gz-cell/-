package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\n=========================================");
        System.out.println("  网上通讯录服务启动成功！运行端口：8080");
        System.out.println("=========================================\n");
    }
}
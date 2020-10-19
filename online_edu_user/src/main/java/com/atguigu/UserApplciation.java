package com.atguigu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.atguigu.edu.mapper")
public class UserApplciation {

    public static void main(String[] args) {
        SpringApplication.run(UserApplciation.class,args);
    }
}

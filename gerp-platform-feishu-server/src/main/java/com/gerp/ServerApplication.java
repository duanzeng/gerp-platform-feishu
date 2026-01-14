package com.gerp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.gerp.platform.feishu.dao")
@ComponentScan({"com.gerp","com.kmniu"})
@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients
@EnableAspectJAutoProxy
public class ServerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ServerApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.ming.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaDemo03Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaDemo03Application.class, args);
    }
}
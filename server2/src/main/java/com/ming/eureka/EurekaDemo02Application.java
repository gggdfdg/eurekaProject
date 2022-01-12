package com.ming.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaDemo02Application {

    public static void main(String[] args) {
        SpringApplication.run(EurekaDemo02Application.class, args);
    }
}
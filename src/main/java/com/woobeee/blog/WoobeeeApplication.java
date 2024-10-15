package com.woobeee.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class WoobeeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(WoobeeeApplication.class, args);
    }

}

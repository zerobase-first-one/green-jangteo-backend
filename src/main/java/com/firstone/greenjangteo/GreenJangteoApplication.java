package com.firstone.greenjangteo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        })
public class GreenJangteoApplication {
    public static void main(String[] args) {
        SpringApplication.run(GreenJangteoApplication.class, args);
    }
}

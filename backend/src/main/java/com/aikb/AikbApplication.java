package com.aikb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aikb.mapper")
public class AikbApplication {

    public static void main(String[] args) {
        SpringApplication.run(AikbApplication.class, args);
    }
}

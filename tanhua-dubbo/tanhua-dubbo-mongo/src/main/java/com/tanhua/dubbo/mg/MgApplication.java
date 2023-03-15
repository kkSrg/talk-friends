package com.tanhua.dubbo.mg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MgApplication {
    public static void main(String[] args) {
        SpringApplication.run(MgApplication.class,args);
    }
}

package com.tanhua.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@MapperScan("com.tanhua.admin.mapper")
@Slf4j
@EnableScheduling
public class AdminServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(AdminServerApplication.class,args);
        log.info("AdminServerApplication start success !!!!!");
    }


}
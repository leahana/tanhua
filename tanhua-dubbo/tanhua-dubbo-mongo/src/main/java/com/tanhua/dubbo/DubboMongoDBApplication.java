package com.tanhua.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 16:58
 */

@Slf4j
@SpringBootApplication
@EnableAsync
public class DubboMongoDBApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboMongoDBApplication.class, args);
        log.info("DubboMongoDBApplication start !!!");
    }
}

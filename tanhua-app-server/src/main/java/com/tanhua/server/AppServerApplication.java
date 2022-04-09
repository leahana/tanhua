package com.tanhua.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: leah_ana
 * @Date: 2022/4/8 22:24
 */
//启动类
@Slf4j
@SpringBootApplication
public class AppServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppServerApplication.class, args);
        log.info("AppServerApplication start success ! ! !");
    }
}

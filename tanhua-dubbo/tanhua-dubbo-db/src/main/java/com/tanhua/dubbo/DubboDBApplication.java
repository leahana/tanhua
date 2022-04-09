package com.tanhua.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 13:43
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.tanhua.dubbo.mappers")
public class DubboDBApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboDBApplication.class, args);
        log.info("DubboDBApplication start success ! ! !");
    }
}

package com.tanhua.autoconfig;

import com.tanhua.autoconfig.properties.*;
import com.tanhua.autoconfig.template.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: leah_ana
 * @Date: 2022/4/8 23:24
 * @Desc: 第三方服务自动装配
 */
@EnableConfigurationProperties({
        SmsProperties.class,
        OssProperties.class,
        AipFaceProperties.class,
        ImProperties.class,
        GreenProperties.class
})
public class TanhuaConfiguration {

    @Bean
    public SmsTemplate smsTemplate(SmsProperties properties) {
        return new SmsTemplate(properties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties properties) {
        return new OssTemplate(properties);
    }

    @Bean
    public ApiFaceTemplate apiFaceTemplate() {
        return new ApiFaceTemplate();
    }

    @Bean
    public ImTemplate imTemplate(ImProperties properties) {
        return new ImTemplate(properties);
    }

    /**
     * 检测文件中是否有tanhua.green开头的配置
     * 同事enable属性为true
     */
    @Bean
    @ConditionalOnProperty(prefix = "tanhua.green",value = "enable", havingValue = "true")
    public AliyunGreenTemplate aliyunGreenTemplate(GreenProperties properties) {
        return new AliyunGreenTemplate(properties);
    }

    @Bean
    public AlibabaGreenTemplate alibabaGreenTemplate(GreenProperties properties) {
        return new AlibabaGreenTemplate(properties);
    }
}

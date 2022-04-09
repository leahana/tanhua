package com.tanhua.autoconfig;

import com.tanhua.autoconfig.properties.SmsProperties;
import com.tanhua.autoconfig.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: leah_ana
 * @Date: 2022/4/8 23:24
 */
@EnableConfigurationProperties({SmsProperties.class})
public class TanhuaConfiguration {
    @Bean
    public SmsTemplate smsTemplate(SmsProperties properties) {
        return new SmsTemplate(properties);
    }
}

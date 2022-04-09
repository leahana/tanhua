package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 0:11
 */



@Data
@ConfigurationProperties(prefix = "tanhua.sms")
public class SmsProperties {
    private String signName;
    private String templateCode;
    private String accessKey;
    private String secret;
}

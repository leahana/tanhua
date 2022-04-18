package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 10:58
 * @Desc: 环信配置
 */

@Configuration
@ConfigurationProperties(prefix = "tanhua.im")
@Data
public class ImProperties {
    private String appKey;
    private String clientId;
    private String clientSecret;

}

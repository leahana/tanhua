package com.tanhua.autoconfig.template;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.tanhua.autoconfig.properties.SmsProperties;

/**
 * @Author: leah_ana
 * @Date: 2022/4/8 23:11
 * @Desc: 阿里云短信发送
 */

public class SmsTemplate {

    private final SmsProperties properties;

    public  SmsTemplate(SmsProperties properties) {
        this.properties = properties;
    }

    public void sendSms(String mobile, String code) {

        String accessKeyId = properties.getAccessKey();
        String accessKeySecret = properties.getSecret();
        System.err.println(accessKeyId+accessKeySecret);

        try {
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(accessKeyId)
                    // 您的AccessKey Secret
                    .setAccessKeySecret(accessKeySecret);
            // 访问的域名
            config.endpoint = "dysmsapi.aliyuncs.com";

            com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(mobile)
                    .setSignName("短信测试")
                    .setTemplateCode("{\"code\":" + code + "}");
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            response.getBody();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}

package com.itheima.test;

import com.tanhua.server.AppServerApplication;
import com.tanhua.autoconfig.template.SmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: leah_ana
 * @Date: 2022/4/8 23:28
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppServerApplication.class})
public class SmsTest {
    @Autowired
    private SmsTemplate smsTemplate;

    @Test
    public void testSendSms() {
        smsTemplate.sendSms("18751909677", "验证码是：123456");
    }
}

package com.tanhua.admin;

/**
 * @Author: leah_ana
 * @Date: 2022/4/16 16:29
 */


import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import com.tanhua.model.domain.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GreenTest {
    @Autowired
    private AliyunGreenTemplate aliyunGreenTemplate;

    @Test
    public void greenTest() throws Exception {
        Map<String, String> map = aliyunGreenTemplate.greenTextScan("今天是个好日子");
        map.forEach((k, v) -> System.out.println(k + "==" + v));
    }
}
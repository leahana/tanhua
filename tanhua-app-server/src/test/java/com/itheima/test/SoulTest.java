package com.itheima.test;

import com.tanhua.model.vo.TestPaperVo;
import com.tanhua.server.AppServerApplication;
import com.tanhua.server.service.TestSoulService;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 16:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class SoulTest {

    @Autowired
    private TestSoulService testSoulService;

    @Test
    public void testGetInformation() {
        List<TestPaperVo> information = testSoulService.getInformation();
        System.err.println(information);
    }
}

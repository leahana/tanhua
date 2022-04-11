package com.tanhua.dubbo;

import com.tanhua.dubbo.utils.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: leah_ana
 * @Date: 2022/4/11 14:56
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdWorkerTest {

    @Autowired
    private IdWorker idWorker;


    @Test
    public void testIdWorker() {
        for (int i = 0; i < 10; i++) {
            System.out.println(idWorker.getNextId("test"));
        }
    }
}

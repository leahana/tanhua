package com.tanhua.admin;

/**
 * @Author: leah_ana
 * @Date: 2022/4/16 16:29
 */


import cn.hutool.core.util.RandomUtil;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.domain.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogTest {

    @Autowired
    private LogMapper logMapper;


    private String logTime;


    @Test
    public void init() {
        String [] months ={ "03", "04"};
        String [] days={"01","02","03","04","06","07","08","09","10","11","12","13","14","15","16"
                ,"17","18","19","20","21","22","23","24","25","26","27","28","29","30"};
        for (String month : months) {
            for (String day : days) {
                this.logTime="2022"+"-"+month+"-"+day;
                generData();
            }
        }

    }

    //模拟登录数据
    public void testInsertLoginLog() {

        for (int i = 0; i < RandomUtil.randomInt(50); i++) {
            Log log = new Log();
            log.setUserId((long) (i + 1));
            log.setLogTime(logTime);
            log.setType("0101");
            logMapper.insert(log);
        }
    }

    //模拟注册数据
    public void testInsertRegistLog() {
        for (int i = 0; i < RandomUtil.randomInt(50); i++) {
            Log log = new Log();
            log.setUserId((long) (i + 1));
            log.setLogTime(logTime);
            log.setType("0102");
            logMapper.insert(log);
        }
    }

    //模拟其他操作
    public void testInsertOtherLog() {
        String[] types = new String[]{"0201", "0202", "0203", "0204", "0205", "0206", "0207", "0301", "0302", "0303", "0304"};
        for (int i = 0; i < RandomUtil.randomInt(50); i++) {
            Log log = new Log();
            log.setUserId((long) (i + 1));
            log.setLogTime(logTime);
            int index = new Random().nextInt(10);
            log.setType(types[index]);
            logMapper.insert(log);
        }
    }


    public void generData() {
        testInsertLoginLog();
        testInsertRegistLog();
        testInsertOtherLog();
    }
}
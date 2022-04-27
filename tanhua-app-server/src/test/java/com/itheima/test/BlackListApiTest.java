package com.itheima.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.api.BlackListApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import com.tanhua.server.service.SettingsService;
import com.tanhua.server.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 16:18
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class BlackListApiTest {
    @DubboReference
    private BlackListApi blackListApi;

    @Autowired
    private SettingsService settingsService;

    @Test
    public  void testQueryBlackList(){

        IPage<UserInfo> userInfoIPage = blackListApi.listBlackList(106L, 1, 10);

        System.out.println(userInfoIPage.getRecords());
    }
}

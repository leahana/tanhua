package com.itheima.test;

import com.tanhua.api.UserApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 13:59
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class UserInfoApiTest {

    @DubboReference
    private UserInfoApi UserInfoApi;

    @Test
    public void testfindByIds() {
        List ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(23);
        //userInfo.set("leah");
        Map byIds = UserInfoApi.findByIds(ids, userInfo);
        byIds.forEach((k, v) -> {
            System.out.println(k + "----" + v);
        });
    }
}

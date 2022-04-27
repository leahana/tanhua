package com.itheima.test;

import com.tanhua.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 13:59
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class UserApiTest {

    @DubboReference
    private UserApi userApi;

    @Test
    public  void testFindByMobile(){
        User user = userApi.getUserByMobile("13800138000");
        System.out.println(user);
    }
}

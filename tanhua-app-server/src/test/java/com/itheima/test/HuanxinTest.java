package com.itheima.test;

import cn.hutool.core.collection.CollUtil;
import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMTextMessage;
import com.tanhua.api.UserApi;
import com.tanhua.autoconfig.template.ImTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.User;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 10:40
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class HuanxinTest {

    private EMService service;

    @Autowired
    private ImTemplate imTemplate;;


    @DubboReference
    private UserApi userApi;

    //@Before
    public void init() {
        EMProperties properties = EMProperties.builder()
                .setAppkey("x")
                .setClientId("Y")
                .setClientSecret("z")
                .build();

       service = new EMService(properties);

    }

    @Test
    public void test() {
//注册用户

//        service.user().create("leahana02", "123456").block();

//        service.contact().add("leahana","leahana02").block();

        //发送消息
        HashSet<String> set = CollUtil.newHashSet("leahana02");
        service.message().send(
                "leahana",
                "users",
                set,
                new EMTextMessage().text("笑了"),
                null)
                .block();

    }

    @Test
    public void testImTemplate() {
                imTemplate
                        .createUser("user03", "123456");
    }


    //批量注册
    @Test
    public void register() {
        List<User> users = userApi.findAll();
        for (User user : users) {
            Boolean create = imTemplate.createUser("hx" + user.getId(), "123456");
            if (create){
                user.setHxUser("hx" + user.getId());
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }
        }
    }
}

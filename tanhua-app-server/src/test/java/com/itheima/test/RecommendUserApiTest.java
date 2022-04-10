package com.itheima.test;

import com.tanhua.api.RecommendUserApi;
import com.tanhua.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
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
public class RecommendUserApiTest {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @Test
    public  void testQuery(){
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(106L);
        System.out.println(recommendUser);
    }
}

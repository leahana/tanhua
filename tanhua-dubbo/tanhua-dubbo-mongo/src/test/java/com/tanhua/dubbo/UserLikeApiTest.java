package com.tanhua.dubbo;

import com.tanhua.api.UserLikeApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 16:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DubboMongoDBApplication.class)
public class UserLikeApiTest {

    @DubboReference
    private UserLikeApi userLikeApi;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Test
    public void test() {

        userLikeApi.countUserLike(106L);
    }


    @Test
    public void add() {

        for (long i = 22; i < 34; i++) {
            UserLike userLike = new UserLike();
            userLike.setId(new ObjectId());
            userLike.setUserId(i);
            userLike.setLikeUserId(106L);
            userLike.setIsLike(true);
            userLike.setCreated(System.currentTimeMillis());
            userLike.setUpdated(System.currentTimeMillis());
            mongoTemplate.save(userLike);
        }

    }
}

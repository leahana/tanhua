package com.tanhua.dubbo.api;

import com.tanhua.api.UserLikeApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 0:13
 */

@DubboService
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;
}

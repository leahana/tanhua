package com.tanhua.dubbo.api;

import com.tanhua.api.RecommendUserApi;
import com.tanhua.model.mongo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:07
 */

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        // 1.根据toUserId查询RecommendUser
        //1.1 构建Criteria
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //1.2 构建Query
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        //1.3 执行查询
        // 2.返回查询结果
        return mongoTemplate.findOne(query, RecommendUser.class);
    }
}

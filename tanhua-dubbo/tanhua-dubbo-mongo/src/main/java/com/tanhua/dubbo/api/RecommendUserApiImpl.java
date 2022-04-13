package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.RecommendUserApi;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public PageResult queryRecommendUserList(Long toUserId, Integer page, Integer pageSize) {

        Criteria criteria = Criteria.where("toUserId").is(toUserId);

        Query query = Query.query(criteria);

        long _count = mongoTemplate.count(query, RecommendUser.class);
        int count = (int) _count;

        query.with(Sort.by(Sort.Order.desc("score"))).
                limit((page - 1) * pageSize).skip(pageSize);

        List<RecommendUser> recommendUserList = mongoTemplate.find(query, RecommendUser.class);

        return new PageResult(page, pageSize, count, recommendUserList);
    }


    @Override
    public RecommendUser queryByUserId(Long userId, Long toUserId) {
        Criteria criteria = Criteria.where("toUserId").is(toUserId).and("userId").is(userId);

        Query query = Query.query(criteria);

        RecommendUser user = mongoTemplate.findOne(query, RecommendUser.class);
        if (user == null) {
            user=new RecommendUser();
            user.setUserId(userId);
            user.setToUserId(toUserId);
            user.setScore(95d);
        }
        return user;
    }
}



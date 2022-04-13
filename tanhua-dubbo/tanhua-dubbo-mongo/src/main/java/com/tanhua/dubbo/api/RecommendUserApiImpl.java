package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.tanhua.api.RecommendUserApi;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
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
            user = new RecommendUser();
            user.setUserId(userId);
            user.setToUserId(toUserId);
            user.setScore(95d);
        }
        return user;
    }


    /**
     * 排除喜欢,不喜欢的用户
     * 随机展示
     * 指定数量
     */
    @Override
    public List<RecommendUser> queryCardList(Long userId, int counts) {
        // 1. 查询 喜欢和不喜欢的用户
        List<UserLike> userLikes = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
        List<Long> likeUserIds = CollUtil.getFieldValues(userLikes, "likeUserId", Long.class);
        // 2.构造查询推荐用户的条件
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(likeUserIds);
        TypedAggregation<RecommendUser> newAggregation =
                TypedAggregation.newAggregation(RecommendUser.class,
                        Aggregation.match(criteria),
                        Aggregation.sample(counts));
        // 3.使用统计函数

        AggregationResults<RecommendUser> results =
                mongoTemplate.aggregate(newAggregation, RecommendUser.class);


        // 4.构造返回
        return results.getMappedResults();
    }
}



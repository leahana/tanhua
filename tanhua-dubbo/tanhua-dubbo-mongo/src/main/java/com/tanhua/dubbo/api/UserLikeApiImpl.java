package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.tanhua.api.UserLikeApi;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 0:13
 */

@DubboService
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike) {
        try {
            // 1.查询数据
            Query query = Query.query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            // 2.如果不存在,保存
            if (userLike == null) {
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setIsLike(isLike);
                mongoTemplate.insert(userLike);
            } else {
                // 3.更新
                Update update = Update.update("isLike", isLike).set("updated", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Integer> queryCounts(Long userId) {
        int eachLoveCount = 0;
        int loveCount = 0;
        int fanCount;
        // 根据用户id查询UserLike表
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("isLike").is(true);
        Query query = Query.query(criteria);

        List<UserLike> likesUser = mongoTemplate.find(query, UserLike.class);

        //用户喜欢
        loveCount = likesUser.size();


        criteria = Criteria.where("likeUserId").is(userId)
                .and("isLike").is(true);
        query = Query.query(criteria);

        //用户粉丝
        List<UserLike> userLikes = mongoTemplate.find(query, UserLike.class);
        fanCount = userLikes.size() == 0 ? 0 : userLikes.size();

        List<Long> likeUserIds = CollUtil.getFieldValues(likesUser, "likeUserId", Long.class);

        List<Long> userLikeIds = CollUtil.getFieldValues(userLikes, "userId", Long.class);

        likeUserIds.retainAll(userLikeIds);
        eachLoveCount = likeUserIds.size();
        HashMap<String, Integer> map = new HashMap<>();

        map.put("eachLoveCount", eachLoveCount);
        map.put("loveCount", loveCount);
        map.put("fanCount", fanCount);
        return map;
    }


    @Override//查询用户喜欢的人
    public List<UserLike> findUserLikes(Long userId) {

        Criteria criteria = Criteria.where("isLike").is(true).and("userId").is(userId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("updated")));
        return mongoTemplate.find(query, UserLike.class);
    }

    @Override
    public List<UserLike> findUserLikes(Long userId, Integer page, Integer pageSize) {
        Criteria criteria = Criteria.where("isLike").is(true).and("userId").is(userId);
        Query query = Query.query(criteria).skip((long) (page - 1) * pageSize)
                .limit(pageSize).with(Sort.by(Sort.Order.desc("updated")));

        return mongoTemplate.find(query, UserLike.class);
    }

    @Override
    public List<UserLike> findUserLikes(Integer page, Integer pageSize, Long likeUserId) {
        Criteria criteria = Criteria.where("isLike").is(true).and("likeUserId").is(likeUserId);
        Query query = Query.query(criteria).skip((long) (page - 1) * pageSize)
                .limit(pageSize).with(Sort.by(Sort.Order.desc("updated")));
        return mongoTemplate.find(query, UserLike.class);
    }



/*

    @Override//2 我关注
    public List<UserLike> findWithType2(String type, Integer page, Integer pageSize, Long userId) {

        Criteria criteria = Criteria.where("isLike").is(true).and("userId").is(userId);
        Query query = Query.query(criteria).skip((long) (page - 1) * pageSize)
                .limit(pageSize).with(Sort.by(Sort.Order.desc("updated")));

        return mongoTemplate.find(query, UserLike.class);
    }

    @Override//3 我的粉丝
    public List<UserLike> findWithType3(String type, Integer page, Integer pageSize, Long userId) {
        Criteria criteria = Criteria.where("isLike").is(true).and("likeUserId").is(userId);
        Query query = Query.query(criteria).skip((long) (page - 1) * pageSize)
                .limit(pageSize).with(Sort.by(Sort.Order.desc("updated")));
        return mongoTemplate.find(query, UserLike.class);
    }
*/

}

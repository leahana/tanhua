package com.tanhua.dubbo.api;

import com.tanhua.api.FriendApi;
import com.tanhua.model.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 16:15
 */

@DubboService
public class FriendApiImpl implements FriendApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Boolean addFriend(Long userId, Long friendId) {


        // 1.保存自己的数据
        //      1.1 判断好友关系是否存在
        Query query1 = Query.query(Criteria
                .where("userId").is(userId)
                .and("friendId").is(friendId));
        if (!mongoTemplate.exists(query1, Friend.class)) {
            Friend userToFriend = new Friend();
            userToFriend.setUserId(userId);
            userToFriend.setFriendId(friendId);
            userToFriend.setCreated(System.currentTimeMillis());
            //      1.2 保存
            mongoTemplate.save(userToFriend);
        }
        // 2.保存好友的数据
        Query query2 = Query.query(Criteria
                .where("userId").is(friendId)
                .and("friendId").is(userId));
        //      2.1 判断好友关系是否存在
        if (!mongoTemplate.exists(query2, Friend.class)) {
            Friend friendToUser = new Friend();
            friendToUser.setUserId(friendId);
            friendToUser.setFriendId(userId);
            friendToUser.setCreated(System.currentTimeMillis());
            //      2.2 保存
            mongoTemplate.save(friendToUser);
        }
        return true;
    }

    @Override
    public List<Friend> queryFriends(Long userId, Integer page, Integer pageSize, String keyword) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria)
                .skip((page - 1) * pageSize)
                .limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Friend.class);

    }
}

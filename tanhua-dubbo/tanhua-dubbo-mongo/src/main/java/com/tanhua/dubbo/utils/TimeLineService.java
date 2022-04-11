package com.tanhua.dubbo.utils;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.MovementTimeLine;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/11 19:02
 */

@Component
public class TimeLineService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Async
    public void saveTimeLine(Long UserId, ObjectId movementId) {
        Criteria criteria = Criteria.where("userId").is(movementId);
        Query query = Query.query(criteria);
        //   2.2查询
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 3.循环好友数据,构建时间线数据存入数据库
        friends.forEach(friend -> {
                    MovementTimeLine movementTimeLine = new MovementTimeLine();
                    movementTimeLine.setMovementId(movementId);
                    movementTimeLine.setUserId(friend.getUserId());
                    movementTimeLine.setFriendId(friend.getFriendId());
                    movementTimeLine.setCreated(System.currentTimeMillis());
                    mongoTemplate.save(movementTimeLine);
                }
        );
    }
}

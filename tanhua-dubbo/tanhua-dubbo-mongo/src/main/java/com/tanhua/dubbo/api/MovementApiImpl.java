package com.tanhua.dubbo.api;

import com.tanhua.api.MovementApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/11 14:46
 */

@DubboService
public class MovementApiImpl implements MovementApi {


    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private IdWorker idWorker;


    @Autowired
    private TimeLineService timeLineService;


    @Override
    public void publishMovement(Movement movement) {
        // 1.保存动态详情
        try {
            //  1.1设置PID
            movement.setPid(idWorker.getNextId("movement"));
            //  1.2设置发布时间
            movement.setCreated(System.currentTimeMillis());
            //movement.setId(ObjectId.get());
            mongoTemplate.save(movement);


            // 2.查询当前用户好友数据
            //   2.1构建查询条件
//            Criteria criteria = Criteria.where("userId").is(movement.getUserId());
//            Query query = Query.query(criteria);
//            //   2.2查询
//            List<Friend> friends = mongoTemplate.find(query, Friend.class);
//            // 3.循环好友数据,构建时间线数据存入数据库
//            friends.forEach(friend -> {
//                        MovementTimeLine movementTimeLine = new MovementTimeLine();
//                        movementTimeLine.setMovementId(movement.getId());
//                        movementTimeLine.setUserId(friend.getUserId());
//                        movementTimeLine.setFriendId(friend.getFriendId());
//                        movementTimeLine.setCreated(System.currentTimeMillis());
//                        mongoTemplate.save(movementTimeLine);
//                    }
//            );

            timeLineService.saveTimeLine(movement.getUserId(), movement.getId());
        } catch (Exception e) {
            //忽略事务处理
            e.printStackTrace();
        }
    }

    @Override
    public PageResult queryMovementsByUserId(Long userId, Integer page, Integer pageSize) {
        // 1.构建Criteria
        Criteria criteria = Criteria.where("userId").is(userId);

        // 2.构建Query
        Query query = Query.query(criteria).skip((long) (page - 1) * pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));

        // 3.查询
        List<Movement> movements = mongoTemplate.find(query, Movement.class);

        return new PageResult(page, pageSize, 0, movements);
    }
}

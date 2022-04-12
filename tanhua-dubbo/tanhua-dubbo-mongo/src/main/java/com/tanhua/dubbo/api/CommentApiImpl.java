package com.tanhua.dubbo.api;

import com.tanhua.api.CommentApi;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/12 13:27
 */

@DubboService
public class CommentApiImpl implements CommentApi {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Integer save(Comment comment) {
        // 1. 查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);

        // 2. 向Comment对象中设置  被评论人
        if (movement != null) {
            comment.setPublishUserId(movement.getUserId());
        }
        // 3. 保存到数据库
        mongoTemplate.save(comment);

        // 4.更新动态表中的对应字段
        Criteria criteria = Criteria.where("id").is(comment.getPublishId().toHexString());
        Query query = Query.query(criteria);
        Update update = new Update();

        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            // 点赞
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            // 评论
            update.inc("commentCount", 1);
        } else {
            //  CommentType.LOVE.getType():
            update.inc("loveCount", 1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();

        options.returnNew(true);//获取更新后的最新数据

        Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
        // 5.获取最新的评论数据返回

        assert modify != null;
        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public List<Comment> queryComments(String publishId, Integer page, Integer pageSize, CommentType comment) {

        Query query = Query.query(
                        Criteria.where("publishId")
                                .is(new ObjectId(publishId))
                                .and("commentType").is(comment.getType()))
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));


        return mongoTemplate.find(query, Comment.class);
    }

    @Override
    public Boolean hasComment(String publishId, Long userId, CommentType like) {
        Criteria criteria = Criteria.where("userid").is(userId)
                .and("publishId").is(new ObjectId(publishId))
                .and("commentType").is(like.getType());
        Query query = Query.query(criteria);
        return mongoTemplate.exists(query, Comment.class);//判断数据是否存在
    }
}

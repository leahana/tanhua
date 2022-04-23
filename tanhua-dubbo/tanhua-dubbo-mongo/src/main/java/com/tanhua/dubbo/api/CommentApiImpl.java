package com.tanhua.dubbo.api;

import com.tanhua.api.CommentApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.CommentOfEvaluate;
import com.tanhua.model.mongo.Movement;
import javafx.scene.shape.Circle;
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
 * @Desc: 评论接口实现
 */

@DubboService
public class CommentApiImpl implements CommentApi {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public Integer saveComment(Comment comment) {
        // 1. 查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);

        // 2. 向Comment对象中设置  被评论人
        if (movement != null) {
            comment.setPublishUserId(movement.getUserId());
        }
        // 3. 保存到数据库
        mongoTemplate.save(comment);

        // 4.更新动态表中的对应字段
        Criteria criteria = Criteria.where("id").
                is(comment.getPublishId());
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
    public List<Comment> pageComments(String publishId, Integer page, Integer pageSize, CommentType comment) {

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

    @Override
    public Integer deleteComment(Comment comment) {
        // 1.删除 Comment表数据
        Criteria criteria = Criteria.where("userid").is(comment.getUserId())
                .and("publishId").is(comment.getPublishId())
                .and("commentType").is(comment.getCommentType());

        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Comment.class);
        // 2.修改动态表中的数量
        // 4.更新动态表中的对应字段

        Query movementQuery = Query.query(Criteria.where("id").
                is(comment.getPublishId()));
        Update update = new Update();

        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            // 点赞
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            // 评论
            update.inc("commentCount", -1);
        } else {
            //  CommentType.LOVE.getType():
            update.inc("loveCount", -1);
        }
        FindAndModifyOptions options = new FindAndModifyOptions();

        options.returnNew(true);//获取更新后的最新数据

        Movement modify = mongoTemplate.findAndModify(movementQuery, update, options, Movement.class);
        // 5.获取最新的评论数据返回


        assert modify != null;
        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public Boolean checkEvaluate(Long userId, String commentId) {
        //查询评论点赞表中是否有数据
        Criteria criteria = Criteria.where("userId").is(userId)
                .and("commentId").is(new ObjectId(commentId));
        Query query = Query.query(criteria);

        return mongoTemplate.exists(query, CommentOfEvaluate.class);//判断数据是否存在
    }

    @Override
    public Integer deleteEvaluate(Long userId, String commentId) {
        Query query = Query.query(Criteria.where("userId").is(userId)
                .and("commentId").is(new ObjectId(commentId)));

        mongoTemplate.remove(query, CommentOfEvaluate.class);
        return countEvalue(commentId, -1);
    }

    @Override
    public Integer saveEvaluate(Long userId, String commentId) {
        CommentOfEvaluate commentOfEvaluate = new CommentOfEvaluate();
        commentOfEvaluate.setUserId(userId);
        commentOfEvaluate.setCommentId(new ObjectId(commentId));
        mongoTemplate.save(new CommentOfEvaluate(idWorker.getNextId(""),
                new ObjectId(commentId), userId));
        return countEvalue(commentId, 1);
    }

    @Override
    public List<Comment> queryCommentUserIds(Long publishUserId, CommentType type, Integer page, Integer pageSize) {

        Criteria criteria = Criteria.where("publishUserId").is(publishUserId);
        if (type == CommentType.LIKE) {
            //点赞
            criteria.and("commentType").is(CommentType.LIKE.getType());
        } else if (type == CommentType.COMMENT) {
            //评论
            criteria.and("commentType").is(CommentType.COMMENT.getType());
        } else {
            //喜欢
            criteria.and("commentType").is(CommentType.LOVE.getType());
        }

        Query query = Query.query(criteria)
                .skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Comment.class);
    }


    //根据评论id查询评论表中的数据
    private Integer countEvalue(String commentId, int type) {

        //根据评论id查询评论表中的数据


        Query query = Query.query(Criteria.where("id").is(new ObjectId(commentId)));

        Update update = new Update();

        update.inc("likeCount", type);

        FindAndModifyOptions options = new FindAndModifyOptions();

        options.returnNew(true);//获取更新后的最新数据

        Comment comment = mongoTemplate.findAndModify(query, update, options, Comment.class);

        // 5.获取最新的评论数据返回
        assert comment != null;
        return comment.getLikeCount();
    }
}

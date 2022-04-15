package com.tanhua.dubbo.api;

import com.mongodb.client.result.UpdateResult;
import com.tanhua.api.VideoCommentApi;
import com.tanhua.model.mongo.VideoComment;
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
 * @Date: 2022/4/15 15:27
 */

@DubboService
public class VideoCommentApiImpl implements VideoCommentApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean checkVideoLike(Long userId, String videoId) {
        Query query = Query.query(Criteria
                .where("userId").is(userId)
                .and("videoId").is(videoId)
                .and("isLike").is(true));

        return mongoTemplate.exists(query, VideoComment.class);
    }

    @Override
    public long upsert(Long userId, String videoId, Boolean isLike) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("videoId").is(new ObjectId(videoId)));
        Update update = new Update()
                .set("isLike", isLike)
                .set("commentType", 0)
                .set("updated", System.currentTimeMillis());
        UpdateResult upsert = mongoTemplate.upsert(query, update, VideoComment.class);

        return upsert.getModifiedCount();
    }

    @Override
    public void save(VideoComment videoComment) {
        videoComment.setLikeCount(0);
        videoComment.setCommentType(1);
        videoComment.setCreated(System.currentTimeMillis());
        videoComment.setUpdated(System.currentTimeMillis());
        mongoTemplate.save(videoComment);
    }

    @Override
    public List<VideoComment> queryComments(String videoId, Integer page, Integer pageSize) {

        Query query = Query.query(Criteria
                .where("videoId").is(new ObjectId(videoId))
                .and("commentType").is(1));
        query.skip((long) (page - 1) * pageSize)
                .limit(pageSize)
                .with(Sort.by(Sort.Order.desc("created")));

        return mongoTemplate.find(query, VideoComment.class);

    }

    @Override
    public void addLike(Long userId, String videoId) {
        this.evalueCount(userId, videoId, 1);
    }

    @Override
    public void deleteLike(Long userId, String videoId) {
        this.evalueCount(userId, videoId, -1);
    }

    private void evalueCount(Long userId, String videoId, int type) {

        //根据评论id查询评论表中的数据
        Query query = Query.query(Criteria
                .where("id").is(new ObjectId(videoId))
                .and("commentType").is(1)
                .and("userId").is(userId));
        Update update = new Update();

        update.inc("likeCount", type);

        FindAndModifyOptions options = new FindAndModifyOptions();

        options.returnNew(true);//获取更新后的最新数据

        VideoComment andModify = mongoTemplate.findAndModify(query, update, options, VideoComment.class);

        // 5.获取最新的评论数据返回
        assert andModify != null;
    }

}

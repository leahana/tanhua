package com.tanhua.dubbo.api;

import com.tanhua.api.VideoApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 20:41
 */
@DubboService
public class VideoApiImpl implements VideoApi {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public String save(Video video) {
        // 1.设置属性
        video.setVid(idWorker.getNextId("video"));
        video.setCreated(System.currentTimeMillis());

        // 2.调用方法保存对象
        mongoTemplate.save(video);

        // 3.返回对象id
        return video.getId().toHexString();
    }

    @Override
    public List<Video> queryMovementsByVids(List<Long> vids) {
        Query query = Query.query(Criteria.where("vid").in(vids));
        return mongoTemplate.find(query, Video.class);
    }

    @Override
    public List<Video> queryVideos(int page, Integer pageSize) {

        Query query = Query.query(new Criteria()).limit(pageSize)
                .skip((long) pageSize * (page - 1))
                .with(Sort.by(Sort.Order.desc("created")));
        return mongoTemplate.find(query, Video.class);
    }
}

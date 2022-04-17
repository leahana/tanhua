package com.tanhua.recommend.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementScore;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.mongo.VideoScore;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/17 11:04
 */

@Component
public class RecommendVideoListener {

    /**
     * 获取动态的日志消息
     * 转化评分
     * 构造评分对象,存入MongoDB
     */

    @Autowired
    private MongoTemplate mongoTemplate;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "tanhua.video.queue",
                    durable = "true"
            ),
            exchange = @Exchange(
                    value = "tanhua.log.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = "log.video"
    ))
    public void recommend(String message) {
        // 1.解析数据

        Map map = JSON.parseObject(message, Map.class);
        map.forEach((k, v) -> {
            System.err.println(k + "=====" + v);
        });
        // 1. 解析map
        Long userId = Long.valueOf(map.get("userId").toString());
        String type = (String) map.get("type");
        String logTime = (String) map.get("logTime");
        String videoId = (String) map.get("busId");

        Video video = mongoTemplate.findById(videoId, Video.class);

        if (video != null) {
            VideoScore videoScore = new VideoScore();

            videoScore.setUserId(userId);
            videoScore.setVideoId(video.getVid());
            videoScore.setDate(System.currentTimeMillis());
            videoScore.setScore(getScore(type));
            mongoTemplate.save(videoScore);
        }


    }

    private static Double getScore(String type) {
        //0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
        Double score = 0d;
        switch (type) {
            case "0301":
                score = 2d;
                break;
            case "0302":
                score = 5d;
                break;
            case "0303":
                score = -5d;
                break;
            case "0304":
                score = 10d;
                break;
            default:
                break;
        }
        return score;
    }
}


package com.tanhua.dubbo.api;

import com.tanhua.api.SoundApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Sound;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.common.value.qual.DoubleVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/19 19:34
 */

@DubboService
public class SoundApiImpl implements SoundApi {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String addSound(String soundUrl, Long userId) {
        if (soundUrl != null && userId != null) {
            Sound sound = new Sound();
            sound.setSoundUrl(soundUrl);
            sound.setUserId(userId);
            sound.setCreated(System.currentTimeMillis());
            sound.setSid(idWorker.getNextId("sound"));
            sound.setUsable(true);
            mongoTemplate.save(sound, "sound");
        }
        return "success";
    }

    @Override
    public Sound randomSound(Long UserId) {
        Criteria criteria = Criteria.where("usable").is(true).and("userId").ne(UserId);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sample(1L)
        );
        List<Sound> sounds = mongoTemplate.aggregate(agg, Sound.class, Sound.class).getMappedResults();
        if (sounds.size() == 0) return new Sound();
        return sounds.get(0);
    }
}

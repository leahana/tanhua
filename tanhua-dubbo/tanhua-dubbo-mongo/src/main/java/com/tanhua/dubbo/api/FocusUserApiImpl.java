package com.tanhua.dubbo.api;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.tanhua.api.FocusUserApi;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Movement;
import feign.form.FormData;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 23:00
 */
@DubboService
public class FocusUserApiImpl implements FocusUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String save(Long userId, Long uid) {
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(userId);
        focusUser.setFollowUserId(uid);
        focusUser.setCreated(System.currentTimeMillis());
        focusUser.setUpdated(System.currentTimeMillis());
        focusUser.setIsDeleted(false);
        FocusUser save = mongoTemplate.save(focusUser);
        return save.getId().toHexString();
    }

    @Override
    public long delete(Long userId, Long uid) {

        Query query = Query.query(Criteria.where("userId").is(userId).and("followUserId").is(uid));
        Update update = new Update();

        update.set("updated", System.currentTimeMillis())
                .set("isDeleted", true);

        UpdateResult upsert = mongoTemplate.upsert(query, update, FocusUser.class);
        return upsert.getMatchedCount();
    }

}

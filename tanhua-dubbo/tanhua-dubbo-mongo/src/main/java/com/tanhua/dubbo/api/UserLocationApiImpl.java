package com.tanhua.dubbo.api;

import com.tanhua.api.UserLocationApi;
import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 11:51
 */

@DubboService
public class UserLocationApiImpl implements UserLocationApi {


    @Autowired
    private MongoTemplate mongoTemplate;


    // 更新
    @Override
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            // 1.根据用户id查询地址信息
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
            // 2.如果不存在位置信息，则插入
            if (userLocation == null) {
                UserLocation location = new UserLocation();
                location.setUserId(userId);
                location.setAddress(address);
                location.setCreated(System.currentTimeMillis());
                location.setCreated(System.currentTimeMillis());
                location.setLastUpdated(System.currentTimeMillis());
                location.setLocation(new GeoJsonPoint(longitude, latitude));
                mongoTemplate.save(location);
            } else {
                // 3.若果存在位置信息，则更新
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("address", address)
                        .set("lastUpdated", System.currentTimeMillis())
                        .set("lastUpdated", userLocation.getUpdated());
                mongoTemplate.updateFirst(query, update, UserLocation.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

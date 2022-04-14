package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.UserLocationApi;
import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

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

    @Override
    public List<Long> queryNearbyUser(Long userId, Double metre) {
        // 1.根据用户id查询用户位置信息

        Query query = Query.query(Criteria.where("userId").is(userId));
        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
        if (userLocation == null) return null;
        // 2.以当前用户的位置为中心 绘制原点
        GeoJsonPoint point = userLocation.getLocation();
        // 3.绘制半径
        Distance distance = new Distance(metre / 1000, Metrics.KILOMETERS);
        // 4.绘制圆形
        Circle circle = new Circle(point, distance);
        // 5.查询
        Query queryLocation = Query.query(Criteria.where("location").withinSphere(circle));

        List<UserLocation> userLocations = mongoTemplate.find(queryLocation, UserLocation.class);

        List<Long> ids = CollUtil.getFieldValues(userLocations, "userId", Long.class);

        return ids;
    }
}

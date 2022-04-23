package com.tanhua.dubbo.api;

import com.tanhua.api.VisitorsApi;
import com.tanhua.model.vo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 17:27
 */
@DubboService
public class VisitorsApiImpl implements VisitorsApi {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存访客数据
     * 对于同一个访客,一天只保存一次
     *
     * @param visitors 访客
     */
    @Override
    public void saveVisitors(Visitors visitors) {
        // 1.构建查询
        Query query = Query.query(Criteria.where("userId").is(visitors.getUserId())
                .and("visitorUserId").is(visitors.getVisitorUserId())
                .and("visitDate").is(visitors.getVisitDate()));

        if (!mongoTemplate.exists(query, Visitors.class)) {
            // 2.不存在.保存
            mongoTemplate.save(visitors);
        }
    }

    //查询首页访客
    @Override
    public List<Visitors> listVisitors(Long date, Long userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        if (date != null) {
            criteria.and("date").gt(date);
        }
        Query query = Query.query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));

        return mongoTemplate.find(query, Visitors.class);
    }

    @Override
    public List<Visitors> pageVisitors(Long userId,  Integer page, Integer pageSize) {
        Criteria criteria = Criteria.where("visitorUserId").is(userId);
        Query query = Query.query(criteria).skip((long) (page - 1) * pageSize).limit(pageSize)
                .with(Sort.by(Sort.Order.desc("date")));
        return mongoTemplate.find(query, Visitors.class);
    }
}

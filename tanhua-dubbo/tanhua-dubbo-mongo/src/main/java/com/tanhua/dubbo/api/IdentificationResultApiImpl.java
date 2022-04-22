package com.tanhua.dubbo.api;

import com.tanhua.api.IdentificationResultApi;
import com.tanhua.model.mongo.IdentificationResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @Author: leah_ana
 * @Date: 2022/4/22 15:55
 */

@DubboService
public class IdentificationResultApiImpl implements IdentificationResultApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public IdentificationResult getResultByType(Integer type) {
        Query query = Query.query(Criteria.where("type").is(type));
        IdentificationResult one = mongoTemplate.findOne(query, IdentificationResult.class);
        System.err.println(one);
        return one;
    }
}

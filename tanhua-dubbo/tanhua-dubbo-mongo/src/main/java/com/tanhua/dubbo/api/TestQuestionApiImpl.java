package com.tanhua.dubbo.api;

import com.tanhua.api.TestQuestionApi;
import com.tanhua.model.mongo.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/22 13:00
 */

@DubboService
public class TestQuestionApiImpl implements TestQuestionApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Question> getQuestions(Long id) {
        Query query = Query.query(Criteria.where("testPaperId").is(id));
        return mongoTemplate.find(query, Question.class);
    }
}

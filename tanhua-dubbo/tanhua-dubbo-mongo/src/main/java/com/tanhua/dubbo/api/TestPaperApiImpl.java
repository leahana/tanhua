package com.tanhua.dubbo.api;

import com.tanhua.api.TestPaperApi;
import com.tanhua.model.mongo.Question;
import com.tanhua.model.mongo.TestPaper;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 15:11
 */

@DubboService
public class TestPaperApiImpl implements TestPaperApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public List<TestPaper> getTestPaper() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("level").is("初级")),
                Aggregation.sample(1)
        );
        List<TestPaper> test_paper1 = mongoTemplate.aggregate(
                aggregation, TestPaper.class, TestPaper.class).getMappedResults();
        aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("level").is("中级")),
                Aggregation.sample(1)
        );
        List<TestPaper> test_paper2 = mongoTemplate.aggregate(
                aggregation, TestPaper.class, TestPaper.class).getMappedResults();

        aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("level").is("高级")),
                Aggregation.sample(1)
        );
        List<TestPaper> test_paper3 = mongoTemplate.aggregate(
                aggregation, TestPaper.class, TestPaper.class).getMappedResults();


        List<TestPaper> test_paper = new ArrayList<>();
        test_paper.add(test_paper1.get(0));
        test_paper.add(test_paper2.get(0));
        test_paper.add(test_paper3.get(0));
        return test_paper;
    }


    @Override
    public Long getTestPaperId(Long questionId) {
        Query query = Query.query(Criteria.where("id").is(questionId));
        System.out.println(questionId.toString());
        Question one = mongoTemplate.findOne(query, Question.class);
        return Objects.requireNonNull(one).getTestPaperId();
    }
}

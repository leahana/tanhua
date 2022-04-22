package com.tanhua.dubbo;

import com.tanhua.api.TestPaperApi;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.domain.KeyValue;
import com.tanhua.model.mongo.IdentificationResult;
import com.tanhua.model.mongo.Option;
import com.tanhua.model.mongo.Question;
import com.tanhua.model.mongo.TestPaper;


import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 16:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DubboMongoDBApplication.class)
public class AddTestPaperTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @DubboReference
    private TestPaperApi testPaperApi;


    @Test
    public void testGetTestPaper() {
        List<TestPaper> testPaper = testPaperApi.getTestPaper();
        System.err.println(testPaper);
    }

    @Test
    public void add() {
        TestPaper testPaper = new TestPaper();
        testPaper.setId(idWorker.getNextId("testPaper"));//试卷id
        testPaper.setName("初级灵魂题");
        testPaper.setCover(" https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_01.png");
        //testPaper.setCover(" https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_02.png");
        //testPaper.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_03.png");
        testPaper.setLevel("初级");
        testPaper.setStar(2); //星别(2-5)
        testPaper.setCreated(System.currentTimeMillis());
        testPaper.setUpdated(System.currentTimeMillis());
        this.getQuestions(testPaper.getId());
        mongoTemplate.save(testPaper);
    }

    //给这张试卷id添加题目
    private void getQuestions(Long id) {
        for (int i = 1; i <= 10; i++) {
            Question question = new Question();
            question.setId(idWorker.getNextId("question") + "");
            question.setTestPaperId(id);
            question.setQuestion("假如你在你房间墙壁上发现一个了小孔，你希望从这个小孔中看如下到什么样的场景？");
            question.setOptions(this.getOptions());
            mongoTemplate.save(question);
        }
    }

    private List<Option> getOptions() {
        List<Option> options = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Option option = new Option();
            option.setId(i + "");
            option.setOption("正在拥抱的一对恋人");
            if (i % 2 == 0) option.setOption("一家人正其乐融融地吃晚餐");
            options.add(option);
        }
        return options;
    }


    @Test
    public void addIdentificationResult() {
        IdentificationResult rs = new IdentificationResult();
        rs.setId(new ObjectId());
        rs.setConclusion("白兔型：平易近人、敦厚可靠、避免冲突与不具批判性。在行为上，表现出不慌不忙、冷静自持的态度。他们注重稳定与中长程规划，现实生活中，常会反思自省并以和谐为中心，即使面对困境，亦能泰然自若，从容应付。");
        rs.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/rabbit.png");
        rs.setType(1);
        KeyValue kv1 = new KeyValue();
        kv1.setKey("外向");
        kv1.setValue("80%");
        KeyValue kv2 = new KeyValue();
        kv2.setKey("判断");
        kv2.setValue("70%");
        KeyValue kv3 = new KeyValue();
        kv3.setKey("抽象");
        kv3.setValue("90%");
        KeyValue kv4 = new KeyValue();
        kv4.setKey("理性");
        kv4.setValue("60%");
        List<KeyValue> list = new ArrayList<>();

        list.add(kv1);
        list.add(kv2);
        list.add(kv3);
        list.add(kv4);

        rs.setDimensions(list);
        mongoTemplate.save(rs);
    }
}

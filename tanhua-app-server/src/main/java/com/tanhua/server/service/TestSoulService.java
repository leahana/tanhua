package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tanhua.api.IdentificationResultApi;
import com.tanhua.api.ReportApi;
import com.tanhua.api.TestPaperApi;
import com.tanhua.api.TestQuestionApi;
import com.tanhua.model.domain.Answer;
import com.tanhua.model.domain.KeyValue;
import com.tanhua.model.domain.Report;
import com.tanhua.model.mongo.IdentificationResult;
import com.tanhua.model.mongo.Question;
import com.tanhua.model.mongo.TestPaper;
import com.tanhua.model.vo.ReportVo;
import com.tanhua.model.vo.TestPaperVo;
import com.tanhua.model.vo.UserVo;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 14:30
 */

@Service
public class TestSoulService {

    @DubboReference
    private TestPaperApi testPaperApi;

    @DubboReference
    private TestQuestionApi testQuestionApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private ReportApi reportApi;

    @DubboReference
    private IdentificationResultApi identificationResultApi;

    public List<TestPaperVo> getInformation() {

        List<TestPaperVo> testPaperVos = new ArrayList<>();
        // 2.如果没有数据，则调用接口获取数据
        //    2.1 获取试卷信息
        List<TestPaper> testPapers = testPaperApi.getTestPaper();
        //    2.2 根据试卷id获取试卷题目信息
        if (CollUtil.isNotEmpty(testPapers)) {
            for (TestPaper testPaper : testPapers) {
                TestPaperVo vo = new TestPaperVo();
                Long testPaperId = testPaper.getId();
                List<Question> questions = testQuestionApi.getQuestions(testPaperId);
                BeanUtils.copyProperties(testPaper, vo, "id");
                vo.setId(testPaperId + "");
                vo.setQuestions(questions);
                //如果试卷为初级 设定为解锁状态
                // 1.先查询redis中是否有数据 今天有没有做过测试灵魂
                String key = "test_soul_" + UserHolderUtil.getUserId();
                String hashKey = testPaperId.toString();
                if ("初级".equals(testPaper.getLevel())
                        // 有做过试卷(都设定为解锁状态) 从redis中或者从mysql中验证
                        || redisTemplate.opsForHash().hasKey(key, hashKey)) {

                    vo.setIsLock(0);
                } else {
                    vo.setIsLock(1);
                }
                // 在mysql中新建测试report
                Long reportId = reportApi.saveReport(testPaperId, UserHolderUtil.getUserId(), vo.getIsLock());
                vo.setReportId(reportId + "");
                testPaperVos.add(vo);
            }
        }

        return testPaperVos;
    }

    public void saveAnswer(Map map) {

        if (map != null) {

            String answers = map.get("answers").toString();
            answers = answers.replaceAll("=", ":");
            List<Answer> questions = JSONArray.parseArray(answers, Answer.class);
            AtomicInteger count = new AtomicInteger();
            String questionId = "";
            for (Answer q : questions) {
                questionId = q.getQuestionId();
                switch (q.getOptionId()) {
                    case "1":
                        count.addAndGet(5);
                        break;
                    case "2":
                        count.addAndGet(4);
                        break;
                    case "3":
                        count.addAndGet(3);
                        break;
                    case "4":
                        count.addAndGet(2);
                        break;
                    default:
                        count.addAndGet(1);
                        break;
                }
            }
            //查询这个题目id绑定的试卷id
            Long testPaperId = testPaperApi.getTestPaperId(Long.valueOf(questionId));
            String key = "test_soul_" + UserHolderUtil.getUserId();
            String hashKey = testPaperId.toString();
            redisTemplate.opsForHash().put(key, hashKey, "1");

            //跟新report 的分数
            reportApi.updateReport(UserHolderUtil.getUserId(), testPaperId, count.get());

        }
        //向redis中保存显示有做过这套试卷
    }

    public ReportVo getReport(String reportId) {

        // 1.查询Mysql中的report 获取分数
        Report reportById = reportApi.findReportById(reportId, UserHolderUtil.getUserId());
        Integer score = null;
        if (reportById != null) {
            score = reportById.getScore();
        }
        Integer type = 0;
        if (score != null) {
            if (score >= 0 && score <= 20) {
                type = 4;
            } else if (score > 20 && score <= 40) {
                type = 3;
            } else if (score > 40 && score <= 60) {
                type = 2;
            } else {
                type = 1;
            }
        }
        System.err.println("分数" + score + "类型" + type);
        type = 1;
        ReportVo reportVo = new ReportVo();
        IdentificationResult resultByType = identificationResultApi.getResultByType(type);

        System.err.println("结果" + resultByType);
        reportVo.setConclusion(resultByType.getConclusion());
        reportVo.setCover(resultByType.getCover());
        reportVo.setDimensions(resultByType.getDimensions());

        List<UserVo> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            UserVo userVo = new UserVo();
            userVo.setAvatar("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_1.png");
            userVo.setId(106 + i);
            list.add(userVo);
        }
        reportVo.setSimilarYou(list);
        return reportVo;
    }
}

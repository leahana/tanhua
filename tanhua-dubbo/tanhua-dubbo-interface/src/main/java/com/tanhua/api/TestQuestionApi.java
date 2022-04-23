package com.tanhua.api;

import com.tanhua.model.mongo.Question;

import java.util.List;

public interface TestQuestionApi {
    // 获取测试试卷的问题
    List<Question> getQuestions(Long id);
}

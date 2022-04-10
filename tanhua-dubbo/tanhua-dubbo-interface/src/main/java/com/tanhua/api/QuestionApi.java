package com.tanhua.api;


import com.tanhua.model.domain.Question;

public interface QuestionApi {

    //根据用户id查询问题
    Question queryQuestionByUserId(Long userId);

    //根据用户id新增问题
    void addQuestion(Question question);

    //根据用户id更新问题
    void updateQuestion(Question question);
}
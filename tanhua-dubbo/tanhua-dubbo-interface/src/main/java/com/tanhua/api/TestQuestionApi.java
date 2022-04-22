package com.tanhua.api;

import com.tanhua.model.mongo.Question;

import java.util.List;

public interface TestQuestionApi {
    List<Question> getQuestions(Long id);
}

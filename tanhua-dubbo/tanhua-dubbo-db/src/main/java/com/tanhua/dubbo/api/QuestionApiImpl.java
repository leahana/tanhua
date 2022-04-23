package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.api.QuestionApi;
import com.tanhua.dubbo.mappers.QuestionMapper;
import com.tanhua.model.domain.Question;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 10:42
 * @Desc: 提问信息实现类
 */

@DubboService
public class QuestionApiImpl implements QuestionApi {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question getQuestion(Long userId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return questionMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveQuestion(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void updateQuestion(Question question) {
        questionMapper.updateById(question);
    }

}

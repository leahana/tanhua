package com.tanhua.server.service;

import com.tanhua.api.QuestionApi;
import com.tanhua.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 10:47
 */

@Service
public class SettingsService {

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private SettingsApi settingsApi;

    //查询通用设置
    public SettingsVo querySettingsById() {
        SettingsVo vo = new SettingsVo();

        Long userId = UserHolderUtil.getUserId();
        vo.setId(userId);
        String mobile = UserHolderUtil.getMobile();
        vo.setPhone(mobile);

        Question question = questionApi.queryQuestionByUserId(userId);
        String questionTxt = question == null ? "" : question.getTxt();
        vo.setStrangerQuestion(questionTxt);

        Settings settings = settingsApi.querySettingsByUserId(userId);
        if (settings != null) {
            //BeanUtils.copyProperties(settings ,vo);
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
            vo.setLikeNotification(settings.getLikeNotification());
        }
        return vo;
    }

    //设置陌生人问题
    public void updateQuestion(String content) {
        // 1.获取用户id
        Long userId = UserHolderUtil.getUserId();

        // 2.调用api查询当前用户的陌生人问题
        Question question = questionApi.queryQuestionByUserId(userId);

        // 3.判断用户是否有陌生人问题
        if (question == null) {
            //3.1 如果没有，则新增
            question=new Question();
            question.setUserId(userId);
            question.setTxt(content);
            questionApi.addQuestion(question);
        }else {
            //3.2 如果有，则修改
            question.setTxt(content);
            questionApi.updateQuestion(question);
        }
    }

    //通知设置
    public void updateNotificationSetting(Map map) {
        // 1.获取用户id
        Long userId = UserHolderUtil.getUserId();
        Boolean gonggaoNotification = (Boolean) map.get("gonggaoNotification");
        Boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        Boolean likeNotification = (Boolean) map.get("likeNotification");

        // 2.调用api查询当前用户的通知设置
        Settings settings = settingsApi.querySettingsByUserId(userId);

        // 3.判断用户是否有通知设置
        if (settings == null) {
            //3.1 如果没有，则新增
            settings=new Settings();
            settings.setUserId(userId);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.addSettings(settings);
        }else {
            //3.2 如果有，则修改
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.updateSettings(settings);
        }
    }
}

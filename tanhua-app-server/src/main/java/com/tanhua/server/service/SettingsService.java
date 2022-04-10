package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.api.BlackListApi;
import com.tanhua.api.QuestionApi;
import com.tanhua.api.SettingsApi;
import com.tanhua.model.domain.*;
import com.tanhua.model.vo.PageResult;
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

    @DubboReference
    private BlackListApi blackListApi;

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
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            questionApi.addQuestion(question);
        } else {
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
            settings = new Settings();
            settings.setUserId(userId);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.addSettings(settings);
        } else {
            //3.2 如果有，则修改
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settingsApi.updateSettings(settings);
        }
    }

    //分页查询黑名单列表
    public PageResult queryBlackList(int page, int size) {
        // 1.获取当前用户id
        Long userId = UserHolderUtil.getUserId();

        // 2.调用api查询用户黑名单分页列表 Mybatis-plus分页对象 IPage<T>
        IPage<UserInfo> iPage = blackListApi.queryBlackListByUserId(userId, page, size);

        // 3.转换为PageResult
        PageResult pageResult =  new PageResult(page, size, (int) iPage.getTotal(), iPage.getRecords());

        return pageResult;
    }

    //取消黑名单
    public void removeBlackList(Long blackUserId) {
        // 1.获取当前用户id
        Long userId = UserHolderUtil.getUserId();

        blackListApi.removeBlackListById(userId, blackUserId);

    }
}

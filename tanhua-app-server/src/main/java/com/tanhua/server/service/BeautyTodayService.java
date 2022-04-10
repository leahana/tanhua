package com.tanhua.server.service;

import com.tanhua.api.RecommendUserApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:28
 */

@Service
public class BeautyTodayService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    //查询今日佳人
    public TodayBest queryTodayBest() {

        // 1.获取用户id
        Long userId = UserHolderUtil.getUserId();
        // 2.调用api查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);

        if (recommendUser == null) {
            recommendUser=new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99d);
        }

        // 2.1 根据 recommendUser 中的用户id 查询推荐用户的id
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        // 3.分装成vo
        TodayBest todayBest = new TodayBest();
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        // 4.返回
        return vo;
    }
}

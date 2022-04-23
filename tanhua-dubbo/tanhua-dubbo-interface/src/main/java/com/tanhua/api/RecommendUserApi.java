package com.tanhua.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:06
 */

public interface RecommendUserApi {

    //查询今日佳人
    RecommendUser getWithMaxScore(Long toUserId);

    //查询好友推荐列表
    PageResult pageRecommendUsers(Long toUserId, Integer page, Integer pageSize);

    //获取推荐用户
    RecommendUser getRecommendUser(Long userId, Long toUserId);

    //获取推荐 小卡片列表
    List<RecommendUser> listRecommendUser(Long userId, int i);
}

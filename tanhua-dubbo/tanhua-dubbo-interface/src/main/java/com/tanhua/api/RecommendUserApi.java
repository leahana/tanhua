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
    RecommendUser queryWithMaxScore(Long toUserId);

    //查询好友推荐列表
    PageResult queryRecommendUserList(Long toUserId, Integer page, Integer pageSize);

    RecommendUser queryByUserId(Long userId, Long toUserId);

    List<RecommendUser> queryCardList(Long userId, int i);
}

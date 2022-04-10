package com.tanhua.api;

import com.tanhua.model.mongo.RecommendUser;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:06
 */

public interface RecommendUserApi {

    //查询今日佳人
    RecommendUser queryWithMaxScore(Long toUserId);
}

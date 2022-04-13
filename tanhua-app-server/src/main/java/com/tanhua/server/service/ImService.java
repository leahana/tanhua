package com.tanhua.server.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.tanhua.api.RecommendUserApi;
import com.tanhua.api.UserApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 12:55
 */

@Service
public class ImService {


    @DubboReference
    private UserApi userApi;

    @DubboReference
    private UserInfoApi userInfoApi;



    public HuanXinUserVo queryUser() {
        Long userId = UserHolderUtil.getUserId();
        User user = userApi.queryById(userId);
        if (user == null) {
            return null;
        }
        return new HuanXinUserVo(user.getHxUser(), user.getHxPassword());
    }

}

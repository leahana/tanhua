package com.tanhua.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.BlackList;
import com.tanhua.model.domain.UserInfo;

public interface BlackListApi {

    //根据用户id查询黑名单
    IPage<UserInfo> queryBlackListByUserId(Long userId, int page, int size);

    void removeBlackListById(Long userId, Long blackUserId);
}
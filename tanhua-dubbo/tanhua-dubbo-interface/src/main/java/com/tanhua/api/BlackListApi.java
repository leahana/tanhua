package com.tanhua.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.BlackList;
import com.tanhua.model.domain.UserInfo;

//黑名单接口
public interface BlackListApi {

    //根据用户id查询黑名单
    IPage<UserInfo> listBlackList(Long userId, int page, int size);

    //删除黑名单
    void deleteBlackList(Long userId, Long blackUserId);
}
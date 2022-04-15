package com.tanhua.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {

    public  void save (UserInfo userInfo);

    public  void update(UserInfo userInfo);

    UserInfo findById(Long id);

    void updateHeader(String imageUrl, Long userId);

    Map<Long,UserInfo> findByIds(List<Long> ids, UserInfo userInfo);

    //分页查询
    IPage findAll(Integer page,Integer pageSize);
}

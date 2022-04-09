package com.tanhua.api;

import com.tanhua.model.domain.UserInfo;

public interface UserInfoApi {

    public  void save (UserInfo userInfo);

    public  void update(UserInfo userInfo);

    UserInfo findById(Long id);
}

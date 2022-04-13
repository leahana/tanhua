package com.tanhua.api;

import com.tanhua.model.domain.User;

import java.util.List;

public interface UserApi {


    //根据手机号码查询用户
    User findByMobile(String mobile);

    //保存用户 返回用户id
    Long save(User user);

    //更新用户手机号
    void updatePhone(String phone,Long id);

    void update(User user);

    User queryById(Long userId);

    User queryByImId(String imId);

    List<User> findAll();
}

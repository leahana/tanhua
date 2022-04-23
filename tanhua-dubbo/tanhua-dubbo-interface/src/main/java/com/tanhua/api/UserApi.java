package com.tanhua.api;

import com.tanhua.model.domain.User;

import java.util.List;

public interface UserApi {


    //根据手机号码查询用户
    User getUserByMobile(String mobile);

    //保存用户 返回用户id
    Long saveUser(User user);

    //更新用户手机号
    void updatePhone(String phone,Long id);

    //更新用户
    void updateUser(User user);

    //根据用户获取用户基本信息
    User getUser(Long userId);

    //根据环信id获取用户id
    User getUserByIm(String imId);

    //获取用户列表
    List<User> listUsers();
}

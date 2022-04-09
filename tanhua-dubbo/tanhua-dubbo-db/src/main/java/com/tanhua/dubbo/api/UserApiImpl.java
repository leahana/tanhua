package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tanhua.api.UserApi;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 13:39
 */
@DubboService
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("mobile", mobile);
        return userMapper.selectOne(qw);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}

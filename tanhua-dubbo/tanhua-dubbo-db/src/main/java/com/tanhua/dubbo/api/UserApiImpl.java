package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tanhua.api.UserApi;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 13:39
 * @Desc: 用户基本服务实现类
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

    @Override
    public void updatePhone(String phone, Long id) {

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("mobile", phone);
        userMapper.update(null, updateWrapper);
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public User queryById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User queryByImId(String imId) {
        QueryWrapper<User> qw = new QueryWrapper<User>()
                .eq("hx_user", imId);

        return userMapper.selectOne(qw);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectList(null);
    }
}

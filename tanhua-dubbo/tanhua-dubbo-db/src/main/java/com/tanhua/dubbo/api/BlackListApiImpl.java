package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.api.BlackListApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.dubbo.mappers.BlackListMapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.BlackList;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 10:40
 * @Desc: 黑名单服务实现类
 */

@DubboService
public class BlackListApiImpl implements BlackListApi {

    @Autowired
    private BlackListMapper blackListMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public IPage<UserInfo> queryBlackListByUserId(Long userId, int page, int size) {

        Page pageParam = new Page(page, size);

        return userInfoMapper.selectPageBlackList(pageParam, userId);
    }

    @Override
    public void removeBlackListById(Long userId, Long blackUserId) {
        QueryWrapper<BlackList> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("black_user_id", blackUserId);
        blackListMapper.delete(queryWrapper);
    }
}

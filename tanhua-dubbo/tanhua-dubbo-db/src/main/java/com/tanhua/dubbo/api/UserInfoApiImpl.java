package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tanhua.api.UserInfoApi;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 19:54
 * @Desc: 用户信息服务实现类
 */

@DubboService
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public void updateHeader(String imageUrl, Long userId) {
        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId).set("avatar", imageUrl);
    }

    @Override
    public Map<Long, UserInfo> findByIds(List<Long> ids, UserInfo userInfo) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        //构造查询条件

        queryWrapper.in(!ids.isEmpty(),"id", ids);

        if (userInfo != null) {
            queryWrapper.eq(!StringUtils.isEmpty(userInfo.getGender()), "gender", userInfo.getGender())
                    .lt(userInfo.getAge() != null, "age", userInfo.getAge())
                    .like(!StringUtils.isEmpty(userInfo.getNickname()),"nikename",userInfo.getNickname());

        }



        List<UserInfo> list = userInfoMapper.selectList(queryWrapper);
        //封装map


        //import cn.hutool.core.collection.CollUtil; 工具类
        Map<Long, UserInfo> map = CollUtil.fieldValueMap(list, "id");

//        HashMap<Long, UserInfo> map = new HashMap<>();
//        list.forEach(temp ->
//                map.put(temp.getId().toString(), temp));
        return map;
    }
}

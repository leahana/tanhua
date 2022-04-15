package com.tanhua.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.api.UserInfoApi;
import com.tanhua.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @Author: leah_ana
 * @Date: 2022/4/15 23:48
 */

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    public PageResult findAllUsers(Integer page, Integer pageSize) {
        IPage iPage = userInfoApi.findAll(page, pageSize);

        return new PageResult(page, pageSize, (int) iPage.getTotal(), iPage.getRecords());
    }

    public UserInfo findUserById(Long userId) {
        return userInfoApi.findById(userId);
    }

    public PageResult findAllMovements(Integer page, Integer pageSize, Long uid, Integer state) {
        return null;
    }


    public PageResult findAllVideos(Integer page, Integer pageSize, Long uid) {
        return videoApi.findByUserId(page, pageSize, uid);
    }
}

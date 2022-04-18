package com.tanhua.server.service;

import com.tanhua.api.UserLocationApi;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 11:53
 * 百度地图服务
 */

@Service
public class BaiduService {

    @DubboReference
    private UserLocationApi userLocationApi;

    /**
     * 更新地理位置
     * @param longitude 经度
     * @param latitude  纬度
     * @param address   地址
     */
    public void updateLocation(Double longitude, Double latitude, String address) {

        Boolean flag = userLocationApi.updateLocation(UserHolderUtil.getUserId(),longitude,latitude,address);
        if(!flag) {
            throw  new BusinessException(ErrorResult.error());
        }
    }
}

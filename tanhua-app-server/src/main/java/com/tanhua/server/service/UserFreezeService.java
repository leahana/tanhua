package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.ErrorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/16 13:17
 */

@Service
public class UserFreezeService {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 判断用户是否被冻结
     * 参数 冻结范围 用户id
     */

    public void checkUserFreeze(String status, Long userId) {
        // 1.拼接key,从redis中查询数据
        String key = Constants.USER_FREEZE + userId;
        String value = redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(value)) {
            Map map = JSON.parseObject(value, Map.class);
            String freezingRange = (String) map.get("freezingRange");
            if (status.equals(freezingRange)) {
                throw new RuntimeException(ErrorResult.builder().errMessage("用户已被冻结").build().toString());
            }
        }
        // 2.如果数据存在且冻结范围一直, 抛出异常
    }


}

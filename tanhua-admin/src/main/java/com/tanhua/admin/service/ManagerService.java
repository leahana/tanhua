package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONGetter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.api.MovementApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.api.VideoApi;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @DubboReference
    private MovementApi movementApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 分页获取用户列表
     */
    public PageResult pageUsers(Integer page, Integer pageSize) {

        // 1.调用api 查询数据:userInfo对象
        IPage<UserInfo> iPage = userInfoApi.pageUserInfos(page, pageSize);
        iPage.getRecords().forEach(userInfo -> {
            String key = Constants.USER_FREEZE + userInfo.getId();
            //判断redis中是否有用户被冻结的标识
                if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                    userInfo.setUserStatus("2");
                }
        });
        return new PageResult(page, pageSize, (int) iPage.getTotal(), iPage.getRecords());
    }

    /**
     * 根据用户id查询用户信息
     * @param userId 用户ID
     * @return UserInfo
     */
    public UserInfo getUser(Long userId) {

        String key = Constants.USER_FREEZE + userId;
        UserInfo userInfo= userInfoApi.findById(userId);
        //判断redis中是否有用户被冻结的标识
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            userInfo.setUserStatus("2");
        }

        return userInfo;
    }

    /**
     * 分页查询用户动态
     */
    public PageResult pageMovements(Integer page, Integer pageSize, Long uid, Integer state) {
        // 1.调用api 查询数据:movement对象
        PageResult pageResult = movementApi.pageMovements(page, pageSize, uid, state);
        // 2.解析PageResult  获取movement对象
        List<Movement> items = (List<Movement>) pageResult.getItems();
        // 3.一个Movement对象转化为一个vo
        if (CollUtil.isEmpty(items)) {
            return new PageResult();
        }
        List<Long> ids = CollUtil.getFieldValues(items, "userId", Long.class);

        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);

        // 4.构造返回值
        List<MovementsVo> vos = new ArrayList<>();
        items.forEach(movement -> {
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        });
        pageResult.setItems(vos);
        return pageResult;
    }

    /**
     * 分页获取用户视频
     * @param uid 用户id
     * @return 自定义分页结果
     */
    public PageResult pageVideos(Integer page, Integer pageSize, Long uid) {
        return videoApi.pageVideos(page, pageSize, uid);
    }


    /**
     * 根据动态id 查询动态详情
     * @param commentId 动态id
     * @return 返回动态Vo对象 包括用户部分信息
     * v1.0 已弃用
     */
    @Deprecated
    public MovementsVo getMovement(String commentId) {
        // 根据动态id查询 动态详情
        Movement movement = movementApi.getMovement(commentId);
        // 根据动态详情查询用户信息
        Map map = new HashMap<>();
        if (movement != null) {
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            if (userInfo != null) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                return vo;

            }
//            Long userId = movement.getUserId();
//            UserInfo byId = userInfoApi.findById(userId);
//            map.put("id", movement.getId().toHexString());
//            map.put("nickname", byId.getNickname());
//            map.put("avatar", byId.getAvatar());
//
//            Date date = new Date(movement.getCreated());
//            int date1 = date.getDate();
//
//            map.put("created", date1);
//
//            map.put("textContent", movement.getTextContent());
//            map.put("imageContent", movement.getMedias());
//            map.put("state", movement.getState());
//            map.put("commentCount", movement.getCommentCount());
//            map.put("likeCount", movement.getLikeCount());


        }
        return new MovementsVo();
    }


    /**
     * 根据动态id 查询动态详情
     * @param commentId 动态id
     * @return 返回动态Vo对象 包括用户部分信息
     */
    public Map getMovement2(String commentId) {
        // 根据动态id查询 动态详情
        Movement movement = movementApi.getMovement(commentId);
        // 根据动态详情查询用户信息
        Map map = new HashMap<>();
        if (movement != null) {
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            if (userInfo != null) {

                map.put("id", movement.getId().toHexString());
                map.put("nickname", userInfo.getNickname());
                map.put("avatar", userInfo.getAvatar());
                Date date = new Date(movement.getCreated());
                map.put("createDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
                map.put("textContent", movement.getTextContent());
                map.put("imageContent", movement.getMedias());
                map.put("state", movement.getState());
                map.put("commentCount", movement.getCommentCount());
                map.put("likeCount", movement.getLikeCount());
            }

        }
        return map;
    }


    /**
     * 用户冻结
     * @param map 用户id 冻结时间
     * @return 是否成功
     */
    public Map updateFreeze(Map map) {
        // 1. 构造key
        String userId = map.get("userId").toString();
        String key = Constants.USER_FREEZE + userId;
        // 2. 构造失效时间
        Integer freezingTime = Integer.valueOf( map.get("freezingTime").toString());

        // 2.1 匹配冻结时间
        int days = 0;
        if (freezingTime == 1) {
            days = 3;
        } else if (freezingTime == 2) {
            days = 7;
        }
        // 3. 将数据存入redis
        String value = JSON.toJSONString(map);
        if (days > 0) {
            redisTemplate.opsForValue().set(key, value, days, TimeUnit.DAYS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
        Map retMap = new HashMap();
        retMap.put("message", "冻结成功");
        return retMap;
    }


    /**
     * 用户解冻
     */
    public Map updateUnfreeze(Map map) {
        String userId = map.get("userId").toString();
        String key = Constants.USER_FREEZE + userId;
        //删除redis中的数据
        redisTemplate.delete(key);
        Map retMap = new HashMap();
        retMap.put("message", "解冻成功");
        return retMap;
    }
}

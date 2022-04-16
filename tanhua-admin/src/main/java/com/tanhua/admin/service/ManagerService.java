package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.api.MovementApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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

    public PageResult findAllUsers(Integer page, Integer pageSize) {
        IPage iPage = userInfoApi.findAll(page, pageSize);

        return new PageResult(page, pageSize, (int) iPage.getTotal(), iPage.getRecords());
    }

    public UserInfo findUserById(Long userId) {
        return userInfoApi.findById(userId);
    }

    //分页查询用户动态
    public PageResult findAllMovements(Integer page, Integer pageSize, Long uid, Integer state) {
        // 1.调用api 查询数据:movement对象
        PageResult pageResult = movementApi.findByUserId(page, pageSize, uid, state);
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


    public PageResult findAllVideos(Integer page, Integer pageSize, Long uid) {
        return videoApi.findByUserId(page, pageSize, uid);
    }

    public MovementsVo findMovementById(String commentId) {
        // 根据动态id查询 动态详情
        Movement movement = movementApi.findByMomentId(commentId);
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

    public Map findMovementById2(String commentId) {
        // 根据动态id查询 动态详情
        Movement movement = movementApi.findByMomentId(commentId);
        // 根据动态详情查询用户信息
        Map map = new HashMap<>();
        if (movement != null) {
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            if (userInfo != null) {

                map.put("id", movement.getId().toHexString());
                map.put("nickname", userInfo.getNickname());
                map.put("avatar", userInfo.getAvatar());
                Date date = new Date(movement.getCreated());
                map.put("createDate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
                map.put("textContent", movement.getTextContent());
                map.put("imageContent", movement.getMedias());
                map.put("state", movement.getState());
                map.put("commentCount", movement.getCommentCount());
                map.put("likeCount", movement.getLikeCount());
            }

        }
        return map;
    }

}

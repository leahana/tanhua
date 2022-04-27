package com.tanhua.api;

import com.tanhua.model.mongo.UserLike;

import java.util.List;
import java.util.Map;

public interface UserLikeApi {

    //保存或更新
    Boolean saveOrUpdate(Long userId, Long likeUserId, Boolean b);

    // 数量统计1 互相关注 2 我关注 3 粉丝 4 谁看过我
    Map<String, Integer> countUserLike(Long userId);

    //分页查找我看过的(重载)
    List<UserLike> listUserLikes(Long userId);

    //分页查找我关注的(重载)
    List<UserLike> listUserLikes(Long userId, Integer page, Integer pageSize);

    //分页查找关注我的(重载)
    List<UserLike> listUserLikes(Integer page, Integer pageSize, Long likeUserId);

    //删除好友
    void deleteFriend(Long userId, Long likeUserId);


    //    //type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
//    List<UserLike> findWithType2(String type, Integer page, Integer pageSize, Long userId);
//
//    //type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
//    List<UserLike> findWithType3(String type, Integer page, Integer pageSize, Long userId);
}

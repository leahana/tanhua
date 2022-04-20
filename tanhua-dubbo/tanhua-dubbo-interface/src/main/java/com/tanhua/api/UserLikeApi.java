package com.tanhua.api;

import com.tanhua.model.mongo.UserLike;

import java.util.List;
import java.util.Map;

public interface UserLikeApi {
    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean b);

    Map<String, Integer> queryCounts(Long userId);

//    //type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
//    List<UserLike> findWithType2(String type, Integer page, Integer pageSize, Long userId);
//
//    //type     1 互相关注 2 我关注 3 粉丝 4 谁看过我
//    List<UserLike> findWithType3(String type, Integer page, Integer pageSize, Long userId);

    List<UserLike> findUserLikes(Long userId);

    //分页查找我关注的
    List<UserLike> findUserLikes(Long userId, Integer page, Integer pageSize);

    //分页查找关注我的
    List<UserLike> findUserLikes(Integer page, Integer pageSize, Long likeUserId);

    void deleteFriend(Long userId, Long likeUserId);
}

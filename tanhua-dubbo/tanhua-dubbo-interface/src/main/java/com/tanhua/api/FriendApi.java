package com.tanhua.api;

import com.tanhua.model.mongo.Friend;

import java.util.List;

public interface FriendApi {

    // 添加好友
    Boolean addFriend(Long userId, Long friendId);

    // 获取好友列表
    List<Friend> listFriends(Long userId, Integer page, Integer pageSize, String keyword);

    // 删除好友
    void removeFriend(Long userId, Long likeUserId);
}

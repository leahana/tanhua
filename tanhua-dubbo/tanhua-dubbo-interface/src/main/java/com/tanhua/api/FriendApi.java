package com.tanhua.api;

import com.tanhua.model.mongo.Friend;

import java.util.List;

public interface FriendApi {

    // 添加好友
    Boolean addFriend(Long userId, Long friendId);

    List<Friend> queryFriends(Long userId, Integer page, Integer pageSize, String keyword);
}

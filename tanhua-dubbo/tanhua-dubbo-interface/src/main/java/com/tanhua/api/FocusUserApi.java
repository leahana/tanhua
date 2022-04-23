package com.tanhua.api;

public interface FocusUserApi {

    // 插入/更新用户关注
    long upsert(Long userId, Long uid, Boolean isFocus);

    // 验证用户是否关注
    Boolean checkUserFocus(Long userId, Long id);
}

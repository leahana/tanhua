package com.tanhua.api;

public interface FocusUserApi {



    long upsert(Long userId, Long uid, Boolean isFocus);

    Boolean checkUserFocus(Long userId, Long id);
}

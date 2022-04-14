package com.tanhua.api;

public interface FocusUserApi {

    String save(Long userId, Long uid);

    long delete(Long userId, Long uid);
}

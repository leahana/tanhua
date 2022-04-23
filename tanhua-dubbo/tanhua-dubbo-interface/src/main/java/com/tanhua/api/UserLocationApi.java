package com.tanhua.api;

import java.util.List;

public interface UserLocationApi {

    //更新用户位置
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);

    //查询附近用户
    List<Long> listUsersNearby(Long id, Double valueOf);
}

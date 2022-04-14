package com.tanhua.api;

import java.util.List;

public interface UserLocationApi {
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);

    List<Long> queryNearbyUser(Long id, Double valueOf);
}

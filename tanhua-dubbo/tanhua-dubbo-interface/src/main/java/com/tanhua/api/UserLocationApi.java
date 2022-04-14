package com.tanhua.api;

public interface UserLocationApi {
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);
}

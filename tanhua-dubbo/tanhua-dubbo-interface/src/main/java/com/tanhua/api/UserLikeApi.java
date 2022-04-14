package com.tanhua.api;

import java.util.Map;

public interface UserLikeApi {
    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean b);

    Map<String, Integer> queryCounts(Long userId);
}

package com.tanhua.api;


import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface MovementApi {

    //发布动态
    String publishMovement(Movement movement);

    List<Movement> queryMovementsByUserId(Long userId, Integer page, Integer pageSize);

    List<Movement> queryFriendsMovements(Long userId, Integer page, Integer pageSize);

    List<Movement> randomMovements(Integer pageSize);

    List<Movement> queryMovementsByPids(List<Long> pids);

    Movement queryByMovementId(String movementId);

    PageResult findByUserId(Integer page, Integer pageSize, Long uid, Integer state);

    Movement findByMomentId(String commentId);

    void updateState(String id, int state);
}
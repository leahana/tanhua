package com.tanhua.api;


import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

public interface MovementApi {

    //发布动态
    void publishMovement(Movement movement);

    PageResult queryMovementsByUserId(Long userId, Integer page, Integer pageSize);
}
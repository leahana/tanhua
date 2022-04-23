package com.tanhua.api;


import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface MovementApi {

    //发布动态
    String publishMovement(Movement movement);

    //批量查询动态
    List<Movement> listMovements(Long userId, Integer page, Integer pageSize);

    //批量查询好友动态
    List<Movement> listFriendsMovements(Long userId, Integer page, Integer pageSize);

    //随机获取动态
    List<Movement> randomMovements(Integer pageSize);

    //根据pid获取动态
    List<Movement> listMovementsByPids(List<Long> pids);

    //获取动态
    Movement getMovement(String movementId);

    //分页获取动态
    PageResult pageMovements(Integer page, Integer pageSize, Long uid, Integer state);

    //根据评论id获取动态
    Movement getMovementByCommentId(String commentId);

    //更新动态审核状态
    void updateState(String id, int state);
}
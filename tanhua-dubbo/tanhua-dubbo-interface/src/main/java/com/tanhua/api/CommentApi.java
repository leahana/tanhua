package com.tanhua.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;

import java.util.List;


public interface CommentApi {


    // 发布评论,并且获取评论数量
    Integer save(Comment comment);

    // 分页查询
    List<Comment> queryComments(String publishId, Integer page, Integer pageSize, CommentType comment);

    //判断comment数据是否存在
    Boolean hasComment(String movementId, Long userId, CommentType like);


    Integer delete(Comment comment);

    Boolean checkEvaluate(Long userId, String movementId);

    Integer deleteEvaluate(Long userId, String movementId);

    Integer saveEvaluate(Long userId, String movementId);
}

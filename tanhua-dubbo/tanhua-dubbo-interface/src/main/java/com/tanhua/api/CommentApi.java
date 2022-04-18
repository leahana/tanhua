package com.tanhua.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;

import java.util.List;


public interface CommentApi {


    // 动态互动,并且获取评论数量(根据类型不同 可判断是保存评论还是点赞)
    Integer save(Comment comment);

    // 取消动态点赞/喜欢
    Integer delete(Comment comment);

    // 分页查询动态评论
    List<Comment> queryComments(String publishId, Integer page, Integer pageSize, CommentType comment);

    // 判断动态互动数据是否存在
    Boolean hasComment(String movementId, Long userId, CommentType like);

    // 判断动态评论互动数据是否存在
    Boolean checkEvaluate(Long userId, String commentId);

    // 保存和动态评论的互动数据(点赞
    Integer saveEvaluate(Long userId, String commentId);

    // 删除和动态评论互动数据(取消点赞
    Integer deleteEvaluate(Long userId, String commentId);

    // 根据动态发布人Id 查询动态评论列表
    List<Comment>  queryCommentUserIds(Long publishUserId, CommentType type, Integer page, Integer pageSize);
}

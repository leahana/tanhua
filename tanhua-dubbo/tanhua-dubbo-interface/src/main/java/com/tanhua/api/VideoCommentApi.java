package com.tanhua.api;

import com.tanhua.model.mongo.VideoComment;

import java.util.List;

public interface VideoCommentApi {

    //查询是否点赞
    Boolean checkVideoLike(Long userId, String videoId);

    //更新或保存点赞状态
    long upsert(Long userId, String videoId,Boolean isLike);

    //保存视频评论
    void saveVideoComment(VideoComment videoComment);

    //获取视频评论
    List<VideoComment> listComments(String videoId, Integer page, Integer pageSize);

    //给视频点赞
    void addLike(Long userId, String videoId);

    //取消视频点赞
    void deleteLike(Long userId, String videoId);
}

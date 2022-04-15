package com.tanhua.api;

import com.tanhua.model.mongo.VideoComment;

import java.util.List;

public interface VideoCommentApi {

    Boolean checkVideoLike(Long userId, String videoId);

    long upsert(Long userId, String videoId,Boolean isLike);

    void save(VideoComment videoComment);

    List<VideoComment> queryComments(String videoId, Integer page, Integer pageSize);

    void addLike(Long userId, String videoId);

    void deleteLike(Long userId, String videoId);
}

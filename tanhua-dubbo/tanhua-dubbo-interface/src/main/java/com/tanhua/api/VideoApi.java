package com.tanhua.api;

import com.tanhua.model.mongo.Video;

import java.util.List;

public interface VideoApi {
    String save(Video video);

    List<Video> queryMovementsByVids(List<Long> vids);

    List<Video> queryVideos(int i, Integer pageSize);

    Boolean checkVideoLike(Long userId, String videoId);

    long upsert(Long userId, String videoId,Boolean isLike);

}

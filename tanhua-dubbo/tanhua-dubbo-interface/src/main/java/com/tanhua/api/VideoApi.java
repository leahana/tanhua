package com.tanhua.api;

import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface VideoApi {
    String save(Video video);

    List<Video> queryMovementsByVids(List<Long> vids);

    List<Video> queryVideos(int i, Integer pageSize);


    PageResult findByUserId(Integer page, Integer pageSize, Long userId);
}

package com.tanhua.api;

import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface VideoApi {

    //保存视频
    String saveVideo(Video video);

    //根据vid查询视频
    List<Video> listMovementsByVids(List<Long> vids);

    //查询视频
    List<Video> listVideos(int i, Integer pageSize);

    //分页查询视频
    PageResult pageVideos(Integer page, Integer pageSize, Long userId);
}

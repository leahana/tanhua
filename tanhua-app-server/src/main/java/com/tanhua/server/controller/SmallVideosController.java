package com.tanhua.server.controller;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 20:42
 */

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.SmallVideosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class SmallVideosController {

    @Autowired
    private SmallVideosService videosService;

    /**
     * 发布视频
     * 接口路径：POST
     * 请求参数：
     * videoThumbnail：封面图
     * videoFile：视频文件
     */
    @PostMapping
    public ResponseEntity saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        videosService.saveVideos(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频列表
     */
    @GetMapping
    public ResponseEntity queryVideoList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = videosService.queryVideoList(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 视频用户关注
     */

    @PostMapping("/{uid}/userFocus")
    public ResponseEntity addUserFocus(@PathVariable("uid") Long uid) {
        videosService.addUserFocus(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 视频用户取消关注
     */
    @PostMapping("/{uid}/userUnFocus")
    public ResponseEntity deleteUserFocus(@PathVariable("uid") Long uid) {
        videosService.deleteUserFocus(uid);
        return ResponseEntity.ok(null);
    }
}
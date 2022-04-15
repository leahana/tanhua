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
import java.util.Map;

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


    /**
     * 视频点赞
     */
    @PostMapping("/{id}/like")
    public ResponseEntity addLike(@PathVariable("id") String id) {

        videosService.addLike(id);

        return ResponseEntity.ok(null);
    }


    /**
     * 视频取消点赞
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity deleteLike(@PathVariable("id") String id) {
        videosService.deleteLike(id);
        return ResponseEntity.ok(null);
    }


    /**
     * 视频评论发布
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity addVideoComments(@PathVariable("id") String videoId, @RequestBody Map map) {
        System.err.println(videoId);
        System.err.println(map);
        String content =(String) map.get("comment");
        videosService.addComments(videoId, content);
        return ResponseEntity.ok(null);
    }


    /**
     * 视频评论点赞
     */
    @PostMapping("/comments/{id}/like")
    public ResponseEntity addCommentsLike(@PathVariable("id") String videoId) {
        videosService.addCommentsLike(videoId);
        return ResponseEntity.ok(null);
    }


    /**
     * 视频评论点赞取消
     */
    @PostMapping("/comments/{id}/dislike")
    public ResponseEntity deleteCommentsLike(@PathVariable("id") String videoId) {
        videosService.deleteCommentsLike(videoId);
        return ResponseEntity.ok(null);
    }


    /**
     * 视频评论查询
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity queryComments(@PathVariable("id") String videoId,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = videosService.queryComments(videoId,page, pagesize);
        return ResponseEntity.ok(result);
    }


}
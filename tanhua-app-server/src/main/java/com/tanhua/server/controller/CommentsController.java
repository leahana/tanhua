package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/12 13:28
 * @Desc: 评论
 */
@RestController
@RequestMapping("/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * 发布评论
     */
    @PostMapping
    public ResponseEntity save(@RequestBody Map map) {

        String movementId = (String) map.get("movementId");
        String comment = (String) map.get("comment");

        commentsService.save(movementId, comment);

        return ResponseEntity.ok(null);
    }

    /**
     * 查询评论列表
     */
    @GetMapping
    public ResponseEntity queryComments(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize,
                                        @RequestParam String movementId) {

        PageResult pageResult = commentsService.queryComments(page, pagesize, movementId);

        return ResponseEntity.ok(pageResult);
    }


    /**
     * 点赞
     */
    @GetMapping("/{id}/like")
    public ResponseEntity likeComment(@PathVariable("id") String movementId) {

        Integer count =commentsService.likeComment(movementId);

        return ResponseEntity.ok(count);
    }

    /**
     * 取消点赞
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislikeComment(@PathVariable("id") String movementId) {

        Integer count =commentsService.dislikeComment(movementId);

        return ResponseEntity.ok(count);
    }

}
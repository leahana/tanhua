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
        //获取动态id
        String movementId = (String) map.get("movementId");
        //获取评论内容
        String comment = (String) map.get("comment");

        commentsService.save(movementId, comment);

        return ResponseEntity.ok(null);
    }


    /**
     * 分页查询评论列表
     */
    @GetMapping
    public ResponseEntity queryComments(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize,
                                        @RequestParam String movementId) {
        PageResult pageResult = commentsService.queryComments(page, pagesize, movementId);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 评论点赞
     */
    @GetMapping("/{id}/like")
    public ResponseEntity likeComment(@PathVariable("id") String commentId) {

        Integer count = commentsService.likeComment(commentId);

        return ResponseEntity.ok(count);
    }


    /**
     * 取消评论点赞
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislikeComment(@PathVariable("id") String commentId) {

        Integer count = commentsService.dislikeComment(commentId);

        return ResponseEntity.ok(count);
    }

}
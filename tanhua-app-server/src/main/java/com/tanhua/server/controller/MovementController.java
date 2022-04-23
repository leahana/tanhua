package com.tanhua.server.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.service.CommentsService;
import com.tanhua.server.service.MovementService;
import org.apache.commons.lang.enums.Enum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/11 15:02
 * @Desc: 动态
 */

@RestController
@RequestMapping("/movements")
public class MovementController {

    @Autowired
    private MovementService movementService;


    @Autowired
    private CommentsService commentsService;


    /**
     * 发布动态
     */
    @PostMapping
    public ResponseEntity saveMovement(Movement movement,
                                      MultipartFile[] imageContent) throws IOException {

        System.err.println("发动态了发动态了");
        movementService.publishMovement(movement, imageContent);

        return ResponseEntity.ok(null);
    }

    /**
     * 查看我的动态
     */
    @GetMapping("/all")
    public ResponseEntity pageMovements(Long userId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pagesize) {

        PageResult pageResult = movementService.pageMovements(userId, page, pagesize);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询好友动态
     */
    @GetMapping
    public ResponseEntity pageFriendsMovement(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer pagesize) {

        PageResult pageResult = movementService.pageFriendsMovement(page, pagesize);

        return ResponseEntity.ok(pageResult);


    }

    /**
     * 查询推荐动态
     */
    @GetMapping("/recommend")
    public ResponseEntity pageRecommendMovements(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer pagesize) {

        PageResult pageResult = movementService.pageRecommendMovements(page, pagesize);

        return ResponseEntity.ok(pageResult);


    }

    /**
     * 查询动态详情
     */
    @GetMapping("/{id}")
    public ResponseEntity getMovement(@PathVariable("id") String movementId) {

        MovementsVo movementsVo = movementService.getMovement(movementId);

        return ResponseEntity.ok(movementsVo);

    }

    /**
     * 点赞
     */
    @GetMapping("/{id}/like")
    public ResponseEntity likeMovement(@PathVariable("id") String movementId) {

        Integer count =commentsService.likeMovement(movementId);

        return ResponseEntity.ok(count);
    }

    /**
     * 取消点赞
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislikeMovement(@PathVariable("id") String movementId) {

        Integer count =commentsService.dislikeMovement(movementId);

        return ResponseEntity.ok(count);
    }

    /**
     * 喜欢
     */
    @GetMapping("/{id}/love")
    public ResponseEntity loveMovement(@PathVariable("id") String movementId) {

        Integer count =commentsService.loveMovement(movementId);

        return ResponseEntity.ok(count);
    }

    /**
     * 取消喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unloveMovement(@PathVariable("id") String movementId) {

        Integer count =commentsService.unloveMovement(movementId);

        return ResponseEntity.ok(count);
    }

    /**
     * 谁看过我
     */
    @GetMapping("visitors")
    public ResponseEntity listVisitors(){
        List<VisitorsVo> list = movementService.listVisitors();
        return ResponseEntity.ok(list);
    }
}

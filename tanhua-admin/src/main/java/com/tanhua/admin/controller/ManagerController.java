package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManagerService;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/15 23:46
 * @Desc: 后台管理相关
 */

/**
 * 查询用户信息
 */
@RestController
@RequestMapping("/manage")
public class ManagerController {

    @Autowired
    protected ManagerService managerService;


    /**
     * 分页查询用户信息
     */
    @GetMapping("/users")
    public ResponseEntity pageUsers(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {

        PageResult result = managerService.pageUsers(page, pagesize);

        return ResponseEntity.ok(result);
    }

    /**
     * 根据id查询
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity getUser(@PathVariable Long userId) {
        UserInfo userInfo = managerService.getUser(userId);
        return ResponseEntity.ok(userInfo);
    }


    /**
     * 查询指定用户发布的所有视频列表
     */
    @GetMapping("/videos")
    public ResponseEntity pageVideos(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pagesize,
                                 Long uid) {
        PageResult result = managerService.pageVideos(page, pagesize, uid);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询指定用户发布的所有视频列表
     * @param uid 用户id
     * @param state 用户状态
     */
    @GetMapping("/messages")
    public ResponseEntity pageMessages(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   Long uid, Integer state) {
        PageResult result = managerService.pageMovements(page, pagesize, uid, state);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据动态id查询用户动态详情
     * @param commentId 动态id
     * @return 动态详情
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity getMessage(@PathVariable("id") String commentId) {
        //  MovementsVo  vo = managerService.findMovementById(commentId);
        Map map = managerService.getMovement2(commentId);

        //System.err.println(map);
        return ResponseEntity.ok(map);
    }


    // 没有收到这个请求
    @GetMapping("/messages/comments")
    public ResponseEntity pageComments(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   String messageID) {
        //根据id查询comment表就行
        System.out.println("根本就没有这个请求"+messageID);
        return ResponseEntity.ok(null);
    }

    /**
     * 用户冻结
     */
    @PostMapping("/users/freeze")
    public ResponseEntity userFreeze(@RequestBody Map params) {
        Map map =  managerService.updateFreeze(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 用户解冻
     */
    @PostMapping("/users/unfreeze")
    public ResponseEntity userUnfreeze(@RequestBody Map params) {
        Map map =  managerService.updateUnfreeze(params);
        return ResponseEntity.ok(map);
    }
}

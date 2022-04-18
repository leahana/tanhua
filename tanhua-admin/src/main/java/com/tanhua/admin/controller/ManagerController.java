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
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = managerService.findAllUsers(page, pagesize);

        return ResponseEntity.ok(result);
    }

    /**
     * 根据id查询
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity findUserById(@PathVariable Long userId) {
        UserInfo userInfo = managerService.findUserById(userId);
        return ResponseEntity.ok(userInfo);
    }


    /**
     * 查询指定用户发布的所有视频列表
     */
    @GetMapping("/videos")
    public ResponseEntity videos(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pagesize,
                                 Long uid) {
        PageResult result = managerService.findAllVideos(page, pagesize, uid);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询指定用户发布的所有视频列表
     * @param page
     * @param pagesize
     * @param uid
     * @param state
     * @return
     */
    @GetMapping("/messages")
    public ResponseEntity messages(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize,
                                   Long uid, Integer state) {
        PageResult result = managerService.findAllMovements(page, pagesize, uid, state);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据冬天id查询用户动态详情
     * @param commentId
     * @return
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity queryMessage(@PathVariable("id") String commentId) {
        //  MovementsVo  vo = managerService.findMovementById(commentId);
        Map map = managerService.findMovementById2(commentId);

        //System.err.println(map);
        return ResponseEntity.ok(map);
    }


    // 没有收到这个请求
    @GetMapping("/messages/comments")
    public ResponseEntity comments(@RequestParam(defaultValue = "1") Integer page,
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
    public ResponseEntity freeze(@RequestBody Map params) {
        Map map =  managerService.userFreeze(params);
        return ResponseEntity.ok(map);
    }

    /**
     * 用户解冻
     */
    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody Map params) {
        Map map =  managerService.userUnfreeze(params);
        return ResponseEntity.ok(map);
    }
}

package com.tanhua.server.controller;

import com.tanhua.commons.utils.Constants;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolderUtil;
import com.tanhua.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 13:40
 */

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 根据环信 用户id 查询用户详情
     */
    @GetMapping("/userinfo")
    public ResponseEntity queryUserInfo(String huanxinId) {
        UserInfoVo vo = messageService.queryUserInfoByIm(huanxinId);
        return ResponseEntity.ok(vo);
    }


    /**
     * 添加好友
     */
    @PostMapping("/contacts")
    public ResponseEntity addContact(@RequestBody Map map) {

        Long friendId = Long.valueOf(map.get("userId").toString());
        messageService.addContact(friendId);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/contacts")
    public ResponseEntity queryFriends(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize,
                                       String keyword) {
        PageResult pageResult = messageService.queryFriends(page, pagesize, keyword);

        return ResponseEntity.ok(pageResult);

    }

    /**
     * 公告分页查询
     */
    @GetMapping("/announcements")
    public ResponseEntity queryAnnouncements(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pageResult = messageService.queryAnnouncements(page, pagesize);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询喜欢列表
     */
    @GetMapping("loves")
    public ResponseEntity queryLoves(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pagesize) {
        // PageResult pageResult = messageService.queryLoves(page, pagesize);
        PageResult pageResult = messageService.messageCommentList(CommentType.LOVE, page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询点赞列表
     */
    @GetMapping("/likes")
    public ResponseEntity queryLikes(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pageResult = messageService.messageCommentList(CommentType.LIKE, page, pagesize);
        return ResponseEntity.ok(pageResult);

    }

    /**
     * 查询评论列表
     */
    @GetMapping("/comments")
    public ResponseEntity queryComments(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pageResult = messageService.messageCommentList(CommentType.COMMENT, page, pagesize);

        return ResponseEntity.ok(pageResult);
    }

}

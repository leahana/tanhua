package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolderUtil;
import com.tanhua.server.service.UserInfoService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 21:44
 */

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 查询用户资料
     * 1.请求头:token
     * 2.请求参数:userID
     */
    @GetMapping
    public ResponseEntity users(@RequestHeader("Authorization") String token, Long userID) {
//        // 1.判断token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken) {
//            return ResponseEntity.status(401).body(null);
//        }
//        // 2.从token中获取用户id并且设置到UserInfo中
//        Claims claims = JwtUtils.getClaims(token);
//        Integer id = (Integer) claims.get("id");
        // 3.判断用户id是否与请求参数中的userID一致

        if (userID == null) {
            userID = UserHolderUtil.getUserId();
        }
       UserInfoVo userInfoVo= userInfoService.findById(userID);
        return  ResponseEntity.ok(userInfoVo);
    }


    /**
     * 更新用户资料
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestHeader("Authorization") String token,
                                         @RequestBody UserInfo userInfo) {
//        // 1.判断token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken) {
//            return ResponseEntity.status(401).body(null);
//        }
//        // 2.从token中获取用户id并且设置到UserInfo中
//        Claims claims = JwtUtils.getClaims(token);
//        Integer id = (Integer) claims.get("id");
        userInfo.setId(UserHolderUtil.getUserId());
        userInfoService.update(userInfo);
        return  ResponseEntity.ok(null);
    }

    /**
     * 更新用户头像
     * 请求参数: 图片文件 headPhoto
     */
    @PostMapping("/header")
    public ResponseEntity updateHeader(MultipartFile headPhoto) throws IOException {
        userInfoService.updateHeader(headPhoto,UserHolderUtil.getUserId());
        return ResponseEntity.ok(null);
    }

}

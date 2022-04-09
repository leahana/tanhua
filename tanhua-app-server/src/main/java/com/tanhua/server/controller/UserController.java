package com.tanhua.server.controller;

import com.sun.org.apache.regexp.internal.RE;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
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
 * @Date: 2022/4/9 19:56
 */

@RestController
@RequestMapping("/user")

public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 保存用户信息
     * 请求头中携带token
     * UserInfo
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo,
                                       @RequestHeader("Authorization") String token) {
//        // 1.判断token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken) {
//            return ResponseEntity.status(401).body(null);
//        }
//        // 2.从token中获取用户id并且设置到UserInfo中
//        Claims claims = JwtUtils.getClaims(token);
//        Integer id = (Integer) claims.get("id");

        userInfo.setId(UserHolderUtil.getUserId());
        // 3.调用service保存用户信息

        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);

    }


    /**
     * 上传用户头像
     * 请求头中携带token
     * MultipartFile文件
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto,
                               @RequestHeader("Authorization") String token) throws IOException {
//        // 1.判断token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken || headPhoto == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        // 2.从token中获取用户id
//        Claims claims = JwtUtils.getClaims(token);
//        Integer id = (Integer) claims.get("id");

        // 3.调用service 保存头像
        userInfoService.updateHead(headPhoto, UserHolderUtil.getUserId());
        return ResponseEntity.ok(null);

    }
}

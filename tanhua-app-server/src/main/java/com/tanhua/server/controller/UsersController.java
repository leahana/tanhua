package com.tanhua.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolderUtil;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.DELETE;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 21:44
 * @Desc: 用户资料验证
 */

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;

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
        UserInfoVo userInfoVo = userInfoService.findById(userID);
        return ResponseEntity.ok(userInfoVo);
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
        return ResponseEntity.ok(null);
    }

    /**
     * 更新用户头像
     * 请求参数: 图片文件 headPhoto
     */
    @PostMapping("/header")
    public ResponseEntity updateHeader(MultipartFile headPhoto) throws IOException {
        userInfoService.updateHead(headPhoto, UserHolderUtil.getUserId());
        return ResponseEntity.ok(null);
    }


    /**
     * 修改手机号:发送验证码
     */
    @PostMapping("/phone/sendVerificationCode")
    public ResponseEntity sendVerificationCode() {
        userService.sendMsg();
        return ResponseEntity.ok(null);
    }


    /**
     * 修改手机号:校验验证码
     */
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity checkVerificationCode(@RequestBody Map map) {
        //int i= 1/0;
        boolean result = true;
        // 1. 获取参数
        String code = (String) map.get("verificationCode");
        // 2. 校验参数
        userService.checkMsg(code);
        // 3.构造返回
        Boolean verification = true;
        Map resultMap = new HashMap();
        resultMap.put("verification", verification);
        return result ? ResponseEntity.ok(resultMap) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("参数错误");
    }


    /**
     * 修改手机号:保存新手机号
     */
    @PostMapping("/phone")
    public ResponseEntity updatePhone(@RequestBody Map map) {

        String phone = (String) map.get("phone");
        // 2. 校验参数
        if (phone == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("参数错误");
        }
        phone = phone.trim();

        // 3. 更新手机号
        userService.updatePhone(phone);
        UserHolderUtil.setMobile(phone);
        return ResponseEntity.ok(null);
    }


    /**
     * 统计数据
     *
     * @return 单项喜欢 单项被喜欢 双向喜欢
     */
    @GetMapping("/counts")
    public ResponseEntity queryCounts() {
        Map<String, Integer> map = userService.queryCounts();
        return ResponseEntity.ok(map);
    }

    /**
     * 互相喜欢,喜欢,粉丝 谁看过我 通用查询 (这个接口文档没有nickname这个参数)
     */
    @GetMapping("/friends/{type}")
    public ResponseEntity queryFriendsWithType(@PathVariable("type") String type,
                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        String nickname = "";
        System.err.println(nickname);
        PageResult pr = userService.queryFriendsWithType(type, page, pageSize);
        return ResponseEntity.ok(pr);
    }

    @PostMapping("/fans/{uid}")
    public ResponseEntity returnFans(@PathVariable("uid") Long userId) {
        userService.returnFans(userId);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/fans/{uid}")
    public ResponseEntity removeFans(@PathVariable("uid") Long userId) {
        userService.removeFans(userId);
        return ResponseEntity.ok(null);
    }
}


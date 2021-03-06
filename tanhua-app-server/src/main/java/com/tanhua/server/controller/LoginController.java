package com.tanhua.server.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.RegEx;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 0:48
 * @Desc: 用户登录
 */

@RestController
@RequestMapping("/user")
public class LoginController {


    @Autowired
    private UserService userService;

    /**
     * 获取登录验证码
     * 请求参数: phone (Map)
     * 响应: void
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        // 1.获取手机号
        String phone = (String) map.get("phone");
        // 2.发送信息
        userService.sendMsg(phone);
        // return ResponseEntity.status(500).body("出错");
        return ResponseEntity.ok("发送成功");
    }

    /**
     * 校验登录
     * 请求参数: phone, code (Map)
     * 响应: ResponseEntity
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map) {
        //int i= 1/0;
        boolean result = true;
        // 1. 获取参数
        String phone = (String) map.get("phone");
        String code = (String) map.get("verificationCode");
        Map resMap = null;
        // 2. 校验验证码参数
        if (phone == null || code == null) {
            result = false;
        } else {
            phone = phone.trim();
            code = code.trim();
            resMap = userService.loginVerification(phone, code);
        }

        // 3.构造返回
        return result ? ResponseEntity.ok(resMap) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("参数错误");

        /*try {
            boolean result = true;
            // 1. 获取参数
            String phone = (String) map.get("phone");
            String code = (String) map.get("verificationCode");
            Map resMap = null;

            // 2. 校验参数
            if (phone == null || code == null) {
                result = false;
            } else {
                phone = phone.trim();
                code = code.trim();
                resMap = userService.loginVerification(phone, code);
            }
            // 3.构造返回
            return result ? ResponseEntity.ok(resMap) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("参数错误");
        } catch (BusinessException be) {
            ErrorResult errorResult = be.getErrorResult();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
        }
*/
    }
}

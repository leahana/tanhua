package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 管理员登录
 */
@RestController
@RequestMapping("/system/users")
public class SystemController {


    @Autowired
    private AdminService adminService;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 生成图片验证码
     */
    @GetMapping("/verification")
    public void saveVerification(String uuid, HttpServletResponse response) throws IOException {
        // 设置响应参数
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        // 1、通过工具类生成验证码对象（图片数据和验证码信息）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(299, 97);
        String code = captcha.getCode();  //1234

        // 2、将验证码存入到redis
        redisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, code);

        // 3、通过输出流输出验证码
        captcha.write(response.getOutputStream());
    }

    /**
     * 用户登录：
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        Map retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 获取用户的信息
     */
    @PostMapping("/profile")
    public ResponseEntity getProfile() {
        AdminVo vo = adminService.getProfile();
        return ResponseEntity.ok(vo);
    }
}

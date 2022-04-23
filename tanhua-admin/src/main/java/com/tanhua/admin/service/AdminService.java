package com.tanhua.admin.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.vo.AdminVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author leah_ana
 * @Desc: 管理员业务层
 */
@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 管理员登录验证码校验
     *
     * @param map
     * @return
     */
    public Map login(Map map) {
        // 1.获取请求参数
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String verificationCode = (String) map.get("verificationCode");

        //用户输入的验证码转换成全小写
        if (!StringUtils.isEmpty(verificationCode)) {
            verificationCode = verificationCode.toLowerCase();
        }

        String uuid = (String) map.get("uuid");

        // 2.校验验证码是否正确
        String key = Constants.CAP_CODE + uuid;
        String value = redisTemplate.opsForValue().get(key);

        //转换redis中的验证码为全小写
        if (!StringUtils.isEmpty(value)) {
            value = value.toLowerCase();
        }

        if (StringUtils.isEmpty(value) || !value.equals(verificationCode)) {
            throw new BusinessException("验证码错误");
        }
        // 3.根据用户名查询管理员对象admin
        Admin admin = adminMapper.selectOne(
                new QueryWrapper<Admin>().eq("username", username));
        password = SecureUtil.md5(password);
        // 4.校验密码是否正确
        if (admin == null || !admin.getPassword().equals(password)) {
            throw new BusinessException("用户名或者密码错误");
        }
        redisTemplate.delete(key);

        // 5.生成token
        Map tokenMap = new HashMap();
        tokenMap.put("id", admin.getId());
        tokenMap.put("username", admin.getUsername());
        String token = JwtUtils.getToken(tokenMap);
        Map retMap = new HashMap();

        retMap.put("token", token);
        return retMap;
    }

    /**
     * 管理员其他信息
     */
    public AdminVo getProfile() {
        Long userId = AdminHolder.getUserId();
        Admin admin = adminMapper.selectById(userId);

        return AdminVo.init(admin);

    }
}

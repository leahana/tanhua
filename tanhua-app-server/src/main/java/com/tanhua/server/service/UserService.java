package com.tanhua.server.service;
import com.tanhua.api.UserApi;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 0:51
 */

@Service
public class UserService {

    private final String CHECK_CODE_KEY = "CHECK_CODE_";

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private UserApi userApi;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    public void sendMsg(String phone) {
        // 1.生成验证码
        //String code = RandomStringUtils.randomNumeric(6);
        String code = "123456";
        // 2.发送短信
        //smsTemplate.sendSms(phone, code);

        // 3.将验证码存入redis,并且设置过期时间为5分钟
        System.out.println("验证码为：" + code);

        redisTemplate.opsForValue().set(CHECK_CODE_KEY + phone, code, Duration.ofMinutes(5));

    }

    /**
     * 校验验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 校验结果
     */
    public Map loginVerification(String phone, String code) {
        // 1.获取redis中的验证码
        String redisCode = redisTemplate.opsForValue().get(CHECK_CODE_KEY + phone);

        // 2.校验验证码是否正确
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            // 验证码错误
//            throw new RuntimeException("验证码错误");
            throw  new BusinessException(ErrorResult.loginError());
        }
        // 3.删除redis中的验证码
        redisTemplate.delete(CHECK_CODE_KEY + phone);
        // 4.通过手机号查询用户信息
        User user = userApi.findByMobile(phone);
        boolean isNew = false;
        // 5.如果用户不存在，则创建用户
        if (user == null) {
            user = new User();
            user.setMobile(phone);
//            user.setCreated(new Date());
//            user.setUpdated(new Date());
            //import cn.hutool.crypto.digest.DigestUtil; 看包名是国人写的加密类
            //DigestUtil.md5Hex("123456");

            //import org.springframework.util.DigestUtils;方法较少
            //DigestUtils.md5Digest("123456".getBytes());

            //import org.springframework.data.redis.core.script.DigestUtils;方法单一看包名与redis有关联
            //DigestUtils.sha1DigestAsHex(Arrays.toString("123456".getBytes()));

            //import org.apache.commons.codec.digest.DigestUtils;方便
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId = userApi.save(user);
            user.setId(userId);
            isNew = true;
        }
        // 6.生成token
        Map tokenMap =new HashMap();
        tokenMap.put("id",user.getId());
        tokenMap.put("mobile",user.getMobile());
        String token = JwtUtils.getToken(tokenMap);
        // 7.返回结果
        Map retMap=new HashMap();
        retMap.put("token",token);
        retMap.put("isNew",isNew);

        return retMap;
    }
}

package com.tanhua.server.service;

import com.tanhua.api.UserApi;
import com.tanhua.api.UserLikeApi;
import com.tanhua.autoconfig.template.ImTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
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

    @DubboReference
    private UserLikeApi userLikeApi;

    @Autowired
    private ImTemplate imTemplate;

    @Autowired
    private UserFreezeService userFreezeService;
    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    public void sendMsg(String phone) {
        // 1.生成验证码
        //String code = RandomStringUtils.randomNumeric(6);
        User user = userApi.findByMobile(phone);
        if (user!= null) {
            userFreezeService.checkUserFreeze("1",user.getId());
        }
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

        //从redis中获取验证码验证,如果验证码不存在或者验证码不正确，则抛出异常
        checkCode(phone, code);

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

            //注册环信用户
            String hxUser = "hx" + user.getId();
            Boolean isCreated = imTemplate.createUser(hxUser, Constants.INIT_PASSWORD);
            if (isCreated) {
                user.setHxUser(hxUser);
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }
        }
        // 6.生成token
        Map tokenMap = new HashMap();
        tokenMap.put("id", user.getId());
        tokenMap.put("mobile", user.getMobile());
        String token = JwtUtils.getToken(tokenMap);
        // 7.返回结果
        Map retMap = new HashMap();
        retMap.put("token", token);
        retMap.put("isNew", isNew);

        return retMap;
    }

    /**
     * 更新手机号
     *
     * @param phone 手机号
     */
    public void updatePhone(String phone) {
        //获取用户id
        Long userId = UserHolderUtil.getUserId();
        userApi.updatePhone(phone, userId);
    }

    /**
     * 更新手机号 给旧手机发送验证码
     */
    public void sendMsg() {
        //获取用户当前用户手机号 发送信息
        String mobile = UserHolderUtil.getMobile();
        this.sendMsg(mobile);
    }

    /**
     * 旧手机号验证码校验
     *
     * @param code 验证码
     */
    public void checkMsg(String code) {
        //获取用户当前用户手机号 发送信息
        String mobile = UserHolderUtil.getMobile();
        //从redis中获取验证码验证,如果验证码不存在或者验证码不正确，则抛出异常
        checkCode(mobile, code);

        //获取用户id
        Long userId = UserHolderUtil.getUserId();

        userApi.updatePhone(mobile, userId);
    }

    /**
     * 校验验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    private void checkCode(String phone, String code) {

        // 1.获取redis中的验证码
        String redisCode = redisTemplate.opsForValue().get(CHECK_CODE_KEY + phone);

        // 2.校验验证码是否正确
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            // 验证码错误
//            throw new RuntimeException("验证码错误");
            throw new BusinessException(ErrorResult.loginError());
        }
        // 3.删除redis中的验证码
        redisTemplate.delete(CHECK_CODE_KEY + phone);
    }

    /**
     * 统计数量
     * 1.双向喜欢数量  eachLoveCount
     * 2.用户单项喜欢数量  loveCount
     * 3.用户被单项喜欢数量 fanCount
     */
    public Map<String, Integer> queryCounts() {
        Long userId = UserHolderUtil.getUserId();
        //1 从redis中获取用户
        //redisTemplate.


        // 2.从mongodb中获取数据
        Map<String, Integer> map = userLikeApi.queryCounts(userId);

        return map;
    }
}

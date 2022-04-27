package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.api.*;
import com.tanhua.autoconfig.template.ImTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserLikeVo;
import com.tanhua.model.vo.Visitors;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.units.qual.A;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private MqMessageService messageService;

    @DubboReference
    private FriendApi friendApi;

    @DubboReference
    private VisitorsApi visitorsApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    public void sendMsg(String phone) {
        // 1.生成验证码
        //String code = RandomStringUtils.randomNumeric(6);
        User user = userApi.getUserByMobile(phone);
        if (user != null) {
            userFreezeService.checkUserFreeze("1", user.getId());
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
        User user = userApi.getUserByMobile(phone);
        boolean isNew = false;
        // 5.如果用户不存在，则创建用户
        String type = "0101";
        if (user == null) {
            type = "0102";
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
            Long userId = userApi.saveUser(user);
            user.setId(userId);
            isNew = true;

            //注册环信用户
            String hxUser = "hx" + user.getId();
            Boolean isCreated = imTemplate.createUser(hxUser, Constants.INIT_PASSWORD);
            if (isCreated) {
                user.setHxUser(hxUser);
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.updateUser(user);
            }
        }
//        try {
//            Map map = new HashMap();
//            map.put("userId", user.getId().toString());
//            map.put("type", type);
//            map.put("logtime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//            String message = JSON.toJSONString(map);
//            amqpTemplate.convertAndSend(
//                    "tanhua.log.exchange",
//                    "log.user",
//                    message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        messageService.sendLogMessage(user.getId(), type, "user", null);

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

        //调用api保存新手机号(手机号在token里面第一次登录已经保存在前端,暂时无法修改)
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
    public Map<String, Integer> listCounts() {

        Long userId = UserHolderUtil.getUserId();
        //1 从redis中获取用户(暂无

        // 2.从mongodb中获取数据

        // 3.返回结果
        return userLikeApi.countUserLike(userId);
    }

    /**
     * 查询用户信息
     *
     * @param type 1 互相关注 2 我关注 3 粉丝 4 谁看过我
     */
    public PageResult pageFriendsWithType(String type, Integer page, Integer pageSize) {
        //  1 互相关注(查询friend表) 2 我关注(user_like表) 3 粉丝(user_like表) 4 谁看过我(查询visitor表)
        // 1.先查询我关注的列表,获取我关注的用户id(并且存入redis,便于对AlreadyLove字段判断
        updateRedis();
        // 2.根据type匹配筛选id
        List<UserLikeVo> vos = new ArrayList<>();

        Map<Long, UserInfo> map;
        if (type == null || type.length() == 0) {
            return new PageResult();
        } else if ("1".equals(type)) {
            //2.1 互相关注(查询friend表)
            List<Friend> friends = friendApi.listFriends(UserHolderUtil.getUserId(), page, pageSize, "");
            if (CollUtil.isEmpty(friends)) return new PageResult();
            List<Long> ids = CollUtil.getFieldValues(friends, "friendId", Long.class);
            map = userInfoApi.findByIds(ids, null);
            friends.forEach(friend -> {
                UserInfo userInfo = map.get(friend.getFriendId());
                if (userInfo != null) {
                    UserLikeVo vo = UserLikeVo.init(userInfo);
                    vo.setAlreadyLove(true);
                    vos.add(vo);
                }
            });

        } else if ("2".equals(type)) {
            //2.2 我关注的
            List<UserLike> userLikes = userLikeApi.listUserLikes(UserHolderUtil.getUserId(), page, pageSize);
            if (CollUtil.isEmpty(userLikes)) return new PageResult();
            List<Long> ids = CollUtil.getFieldValues(userLikes, "likeUserId", Long.class);
            map = userInfoApi.findByIds(ids, null);
            userLikes.forEach(userLike -> {
                UserInfo userInfo = map.get(userLike.getLikeUserId());
                if (userInfo != null) {
                    UserLikeVo vo = UserLikeVo.init(userInfo);
                    vo.setAlreadyLove(true);
                    vos.add(vo);
                }
            });

/*            String key = "user_like_" + UserHolderUtil.getUserId();
            String redisValue = redisTemplate.opsForValue().get(key);
            if (!org.springframework.util.StringUtils.isEmpty(redisValue)) {
                // 3.如果redis数据存在,根据VID 查询数据
                String[] split = redisValue.split(",");
                // 判断当前页的起始条数是否小于数组总数
                if ((page - 1) * pageSize < split.length) {
                    List<Long> baseIds = Arrays.stream(split).skip((long) (page - 1) * pageSize).limit(pageSize)
                            .map(Long::parseLong).collect(Collectors.toList());
                        Map<Long, UserInfo> map = userInfoApi.findByIds(baseIds, null);
                }
            }
            */

        } else if ("3".equals(type)) {
            //2.3 粉丝
            updateRedis();
            List<UserLike> userLikes = userLikeApi.listUserLikes(page, pageSize, UserHolderUtil.getUserId());
            if (CollUtil.isEmpty(userLikes)) return new PageResult();
            List<Long> ids = CollUtil.getFieldValues(userLikes, "userId", Long.class);
            map = userInfoApi.findByIds(ids, null);
            List<String> redisUserIds = getRedisUserIds();
            System.err.println(redisUserIds);
            userLikes.forEach(userLike -> {
                UserInfo userInfo = map.get(userLike.getUserId());
                if (userInfo != null) {
                    UserLikeVo vo = UserLikeVo.init(userInfo);
                    if (redisUserIds.contains(userLike.getUserId().toString())) {
                        vo.setAlreadyLove(true);
                    } else {
                        vo.setAlreadyLove(false);
                    }
                    vos.add(vo);
                }
            });
        } else {
            // 查询visitor
            List<Visitors> visitorsList = visitorsApi.pageVisitors(UserHolderUtil.getUserId(), page, pageSize);
            if (CollUtil.isEmpty(visitorsList)) return new PageResult();
            List<Long> ids = CollUtil.getFieldValues(visitorsList, "userId", Long.class);
            map = userInfoApi.findByIds(ids, null);
            List<String> redisUserIds = getRedisUserIds();
            visitorsList.forEach(visitors -> {
                UserInfo userInfo = map.get(visitors.getUserId());
                if (userInfo != null) {
                    UserLikeVo vo = UserLikeVo.init(userInfo);
                    if (redisUserIds.contains(visitors.getUserId().toString())) {
                        vo.setAlreadyLove(true);
                    } else {
                        vo.setAlreadyLove(false);
                    }
                    vos.add(vo);
                }
            });
        }


        // 3.封装vo对象


        // 4.返回结果

        return new PageResult(page, pageSize, 0, vos);
    }

    /**
     * 更新redis中的数据
     */
    private void updateRedis() {
        List<UserLike> myLikeList = userLikeApi.listUserLikes(UserHolderUtil.getUserId());
        System.err.println("myLikeList = " + myLikeList);
        if (CollUtil.isNotEmpty(myLikeList)) {
            // 获取我关注的用户id 存入redis
            String key = "user_like_" + UserHolderUtil.getUserId();
            String s = redisTemplate.opsForValue().get(key);
            System.err.println(s);
            List<Long> baseIds = CollUtil.getFieldValues(myLikeList, "likeUserId", Long.class);
            System.err.println("baseIds = " + baseIds);
            redisTemplate.opsForValue().set(key, baseIds.toString(), 60, TimeUnit.SECONDS);
            System.err.println(redisTemplate.opsForValue().get(key));
        }
    }

    /**
     * 获取redis中的用户id
     */
    private List<String> getRedisUserIds() {
        String key = "user_like_" + UserHolderUtil.getUserId();
        String redisValue = redisTemplate.opsForValue().get(key);
        List<String> strings = new ArrayList<>();
        if (!org.springframework.util.StringUtils.isEmpty(redisValue)) {
            // 3.如果redis数据存在,根据VID 查询数据


            String[] split = redisValue.substring(1, redisValue.length() - 1).split(",");

            strings.addAll(Arrays.asList(split));
        }
        return strings;
    }

    /**
     * 回关
     *
     * @param likeUserId 粉丝id
     */
    public void saveFans(Long likeUserId) {
        //1.给user_like表添加数据
        Boolean orUpdate = userLikeApi.saveOrUpdate(UserHolderUtil.getUserId(), likeUserId, true);
        //2.添加环信好友并且给friend表添加数据
        Boolean aBoolean = imTemplate.addContact("hx" + UserHolderUtil.getUserId(), "hx" + likeUserId);
        Boolean aBoolean1 = friendApi.addFriend(UserHolderUtil.getUserId(), likeUserId);
        if (!aBoolean || !orUpdate || !aBoolean1) {
            throw new RuntimeException("添加好友失败");
        }
    }

    /**
     * 取消回关
     *
     * @param likeUserId 粉丝id
     */
    public void deleteFans(Long likeUserId) {
        // 1.更新user_like表 为false
        Boolean aBoolean = userLikeApi.saveOrUpdate(UserHolderUtil.getUserId(), likeUserId, false);
        // 2.删除friend表
        friendApi.removeFriend(UserHolderUtil.getUserId(), likeUserId);
        // 3.删除环信好友
        Boolean aBoolean1 = imTemplate.deleteContact("hx" + UserHolderUtil.getUserId(), "hx" + likeUserId);

        if (!aBoolean || !aBoolean1) {
            throw new RuntimeException("删除好友失败");
        }


    }




  /*
    public PageResult queryFriendsWithType_1(String type, String nickname, Integer page, Integer pageSize) {

        //根据用户本人用户id查询自己喜欢的用户Id
        List<UserLike> likes = userLikeApi.findWithType2(type, page, pageSize, UserHolderUtil.getUserId());

        List<Long> ids = CollUtil.getFieldValues(likes, "likeUserId", Long.class);

        String key = "userLike_" + UserHolderUtil.getUserId();

        redisTemplate.opsForValue().set(key, ids.toString(), 60, TimeUnit.SECONDS);

        if (StringUtils.isEmpty(type)) throw new BusinessException(ErrorResult.error());

        List<UserLikeVo> vos;

        if ("1".equals(type)) {
            //互相关注
            List<Friend> friendList = friendApi.queryFriends(UserHolderUtil.getUserId(), page, pageSize, "");
            if (CollUtil.isEmpty(friendList)) return new PageResult();
            vos = getVos1(friendList, nickname);

        } else if ("2".equals(type)) {
            List<UserLike> userLikeList = userLikeApi.findWithType2(type, page, pageSize, UserHolderUtil.getUserId());
            if (CollUtil.isEmpty(userLikeList)) return new PageResult();
            vos = getVos2(userLikeList, nickname);

        } else if ("3".equals(type)) {
            List<UserLike> userLikeList = userLikeApi.findWithType3(type, page, pageSize, UserHolderUtil.getUserId());
            if (CollUtil.isEmpty(userLikeList)) return new PageResult();
            vos = getVos3(userLikeList, nickname);

        } else {
            List<Visitors> visitorsList = visitorsApi.queryVisitorsWithPage(UserHolderUtil.getUserId(), page, pageSize);
            if (CollUtil.isEmpty(visitorsList)) return new PageResult();
            vos = getVos4(visitorsList, nickname);
        }

        return new PageResult(page, pageSize, 0, vos);
    }

    private List<UserLikeVo> getVos3(List<UserLike> userLikeList, String nickname) {
        List<UserLikeVo> vos = new ArrayList<>();
        String key = "userLike_" + UserHolderUtil.getUserId();
        String value = redisTemplate.opsForValue().get(key);
        List<Long> values = JSON.parseArray(value, Long.class);
        List<Long> ids = CollUtil.getFieldValues(userLikeList, "userId", Long.class);
        UserInfo userInfo = new UserInfo();
        if (StringUtils.isNotEmpty(nickname)) userInfo.setNickname(nickname);
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        userLikeList.forEach(temp -> {
            UserInfo ufo = map.get(temp.getId());
            if (ufo != null) {
                UserLikeVo vo = UserLikeVo.init(ufo);
                assert values != null;
                if (values.contains(ufo.getId())) {
                    vo.setAlreadyLove(true);
                } else {
                    vo.setAlreadyLove(false);
                }
                vos.add(vo);
            }
        });
        return vos;
    }


    private List<UserLikeVo> getVos4(List<Visitors> visitorsList, String nickname) {
        List<UserLikeVo> vos = new ArrayList<>();
        List<Long> ids = CollUtil.getFieldValues(visitorsList, "userId", Long.class);
        UserInfo userInfo = new UserInfo();
        if (StringUtils.isNotEmpty(nickname)) userInfo.setNickname(nickname);
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        visitorsList.forEach(visitors -> {
            UserInfo ufo = map.get(visitors.getUserId());
            if (ufo != null) {
                UserLikeVo vo = UserLikeVo.init(ufo);
                vos.add(vo);
            }
        });
        return vos;
    }

    private List<UserLikeVo> getVos2(List<UserLike> userLikeList, String nickname) {
        List<UserLikeVo> vos = new ArrayList<>();
        List<Long> ids = CollUtil.getFieldValues(userLikeList, "userLikeId", Long.class);
        UserInfo userInfo = new UserInfo();
        if (StringUtils.isNotEmpty(nickname)) userInfo.setNickname(nickname);
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        userLikeList.forEach(temp -> {
            UserInfo ufo = map.get(temp.getLikeUserId());
            if (ufo != null) {
                UserLikeVo vo = UserLikeVo.init(ufo);
                vo.setAlreadyLove(true);
                vos.add(vo);
            }
        });
        return vos;
    }

    private List<UserLikeVo> getVos1(List<Friend> friendList, String nickname) {
        List<UserLikeVo> vos = new ArrayList<>();
        List<Long> ids = CollUtil.getFieldValues(friendList, "userId", Long.class);
        UserInfo userInfo = new UserInfo();
        if (StringUtils.isNotEmpty(nickname)) userInfo.setNickname(nickname);
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        friendList.forEach(temp -> {
            UserInfo ufo = map.get(temp.getUserId());
            if (ufo != null) {
                UserLikeVo vo = UserLikeVo.init(ufo);
                vo.setAlreadyLove(true);
                vos.add(vo);
            }
        });
        return vos;
    }

*/
}

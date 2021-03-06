package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.tanhua.api.*;
import com.tanhua.autoconfig.template.ImTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.*;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.Near;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:28
 * @Desc: 推荐
 */

@Service
public class RecommendService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private UserLikeApi userLikeApi;

    @DubboReference
    private UserLocationApi locationApi;

    @DubboReference
    private VisitorsApi visitorsApi;

    @Autowired
    private ImTemplate imTemplate;

    @Value("${tanhua.default.recommend.users}")
    private String recommendUser;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MessageService messageService;


    /**
     * 查询今日佳人
     *
     * @return 今日佳人
     */
    public TodayBest getTodayBest() {

        // 1.获取用户id
        Long userId = UserHolderUtil.getUserId();

        // 2.调用api查询
        RecommendUser recommendUser = recommendUserApi.getWithMaxScore(userId);

        // 判断 如果没有推荐用户就构造一个默认用户
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99d);
        }

        // 2.1 根据 recommendUser 中的用户id 查询推荐用户的id
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());

        // 3.封装成vo
        TodayBest todayBest = new TodayBest();

        // 4.返回结果
        return TodayBest.init(userInfo, recommendUser);
    }

    /**
     * 查询推荐好友
     * @param recommendUserDto 推荐好友参数
     * @return 分页的  推荐好友
     */
    public PageResult listRecommendationFriends(RecommendUserDto recommendUserDto) {

        // 1.获取用户id
        Long userId = UserHolderUtil.getUserId();
        Integer page = recommendUserDto.getPage();
        Integer pageSize = recommendUserDto.getPageSize();

        // 2.获取分页中的RecommendUsers
        PageResult pageResult = recommendUserApi.pageRecommendUsers(userId, page, pageSize);
        List<RecommendUser> recommendUsers = (List<RecommendUser>) pageResult.getItems();

        // 3.判断分页列表似乎否为空
        if (recommendUsers == null) return pageResult;

        List<TodayBest> todayBestList = new ArrayList<>();

        // 4.提取所有推荐用户的id
        List<Long> ids = CollUtil.getFieldValues(recommendUsers, "userId", Long.class);


        //结果为空 给他推荐小小难顶!
        if (CollUtil.isEmpty(ids) || ids.size() == 0) {
            RecommendUser recommendUser = new RecommendUser();
            UserInfo info = userInfoApi.findById(106L);
            TodayBest vo = TodayBest.init(info, recommendUser);
            todayBestList.add(vo);
            pageResult.setItems(todayBestList);
            return pageResult;
        }
//        List<Long> ids = recommendUsers.stream()
//                .map(RecommendUser::getUserId).collect(Collectors.toList());

        // 5.构建查询条件,批量查询
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(recommendUserDto.getAge());
        userInfo.setGender(recommendUserDto.getGender());
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        // 6.构建vo对象
        recommendUsers.forEach(item -> {
            UserInfo info = map.get(item.getUserId());
            if (info != null) {
                TodayBest vo = TodayBest.init(info, item);
                todayBestList.add(vo);
            }
        });

//        for (RecommendUser temp : userInfos) {
//            Long recommendUserId = temp.getUserId();
//            UserInfo userInfo = userInfoApi.findById(recommendUserId);
//            if (userInfo != null) {
//                //条件判断
//                if (!StringUtils.isEmpty(recommendUserDto.getGender())
//                        && ! recommendUserDto.getGender().equals(userInfo.getGender())) {
//                    continue;
//                }
//                if (null != recommendUserDto.getAge()
//                        && recommendUserDto.getAge() < userInfo.getAge()) {
//                    continue;
//                }
//                TodayBest vo = TodayBest.init(userInfo, temp);
//                todayBestList.add(vo);
//            }
//        }
        pageResult.setItems(todayBestList);
        return pageResult;

    }


    /**
     * 查看佳人信息
     * @param userId 佳人用户id
     * @return 佳人信息
     */
    public TodayBest getPersonalInfo(Long userId) {

        // 1.根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(userId);

        // 2.根据操作人id和查看用户的id查询缘分值
        RecommendUser recommendUser = recommendUserApi.getRecommendUser(userId, UserHolderUtil.getUserId());


        //访客数据
        Visitors visitors = new Visitors();
        visitors.setUserId(userId);
        visitors.setVisitorUserId(UserHolderUtil.getUserId());
        visitors.setFrom("首页");
        visitors.setDate(System.currentTimeMillis());
        visitors.setVisitDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        visitors.setScore(recommendUser.getScore());
        visitorsApi.saveVisitors(visitors);

        // 3.构造返回值

        return TodayBest.init(userInfo, recommendUser);
    }

    /**
     * 查询用户默认问题
     * @param userId 用户id
     * @return 默认问题
     */
    public String listQuestions(Long userId) {
        Question question = questionApi.getQuestion(userId);
        //若果没有默认问题, 则构造默认问题为"你是?"
        return question == null ? "你是?" : question.getTxt();
    }

    /**
     * 回复陌生人问题
     * @param userId 用户id
     * @param reply 回复内容
     */
    public void replyQuestions(Long userId, String reply) {
        // 1. 构造消息数据
        Long id = UserHolderUtil.getUserId();
        UserInfo userinfo = userInfoApi.findById(id);

        // 2.构造消息详细信息
        Map map = new HashMap();
        map.put("userId", id);
        map.put("huanxinId", Constants.HX_USER_PREFIX + id);
        map.put("nickname", userinfo.getNickname());
        map.put("strangerQuestion", listQuestions(userId));
        map.put("reply", reply);

        // 转换map对象为JSON字符串
        String messge = JSONObject.toJSONString(map);

        // 2.调用template对象.发送消息
        Boolean aBoolean = imTemplate.sendMsg(
                Constants.HX_USER_PREFIX + userId, messge);

        // 3.判断环信消息是否发送成功
        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }

    }

    /**
     * 查询探花推荐
     * @return 今日佳人卡片集合
     */
    public List<TodayBest> listRecommends() {

        // 1.调用 推荐api查询数据列表(排除喜欢/不喜欢, 数量限制

        List<RecommendUser> userList = recommendUserApi.listRecommendUser(UserHolderUtil.getUserId(), 10);

        // 2.判断数据是否存在,不存在默认构造
        if (CollUtil.isEmpty(userList)) {
            userList = new ArrayList<>();
            String[] userIdS = recommendUser.split(",");
            for (String userId : userIdS) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(userId));
                recommendUser.setToUserId(UserHolderUtil.getUserId());
                recommendUser.setScore(RandomUtil.randomDouble(60, 90));
                userList.add(recommendUser);
            }
        }
        // 3.构造vo
        //      3.1.提取用户id
        List<Long> ids = CollUtil.getFieldValues(userList, "userId", Long.class);
        //      3.2.查询用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);
        //      3.3.构造vo
        List<TodayBest> todayBestList = new ArrayList<>();
        userList.forEach(item -> {
            UserInfo userInfo = map.get(item.getUserId());
            if (userInfo != null) {
                TodayBest vo = TodayBest.init(userInfo, item);
                todayBestList.add(vo);
            }
        });
        // 4.返回结果
        return todayBestList;
    }

    /**
     * 查询探花卡片滑动喜欢
     * @param likeUserId 喜欢的用户id
     */
    public void likeUser(Long likeUserId) {

        // 1. 保存喜欢数据到MongoDB中
        Boolean save = userLikeApi.saveOrUpdate(UserHolderUtil.getUserId(), likeUserId, true);
        if (!save) {
            throw new BusinessException(ErrorResult.error());
        }

        // 2. 操作redis 写入喜欢的数据
        redisTemplate.opsForSet().remove(
                Constants.USER_NOT_LIKE_KEY + UserHolderUtil.getUserId(),
                likeUserId.toString());
        redisTemplate.opsForSet().add(
                Constants.USER_LIKE_KEY + UserHolderUtil.getUserId(),
                likeUserId.toString());

        // 3. 判断是否是双向喜欢
        if (isLike(likeUserId, UserHolderUtil.getUserId())) {
            // 4. 添加好友
            messageService.addContact(likeUserId);
        }
    }

    /**
     * 查询探花卡片滑动不喜欢
     * @param likeUserId  不喜欢的用户id
     */
    public void dislikeUser(Long likeUserId) {
        // 1. 保存喜欢数据到MongoDB中

        Boolean save = userLikeApi.saveOrUpdate(UserHolderUtil.getUserId(), likeUserId, false);
        if (!save) {
            throw new BusinessException(ErrorResult.error());
        }

        // 2. 操作redis 写入喜欢的数据
        redisTemplate.opsForSet().add(
                Constants.USER_NOT_LIKE_KEY + UserHolderUtil.getUserId(),
                likeUserId.toString());
        redisTemplate.opsForSet().remove(
                Constants.USER_LIKE_KEY + UserHolderUtil.getUserId(),
                likeUserId.toString());

        // 3. 判断是否双向喜欢 如果喜欢 删除好友
        if (isLike(likeUserId, UserHolderUtil.getUserId())) {
            // 4. 删除好友
            //      4.1.删除mongodb中的数据
            userLikeApi.deleteFriend(UserHolderUtil.getUserId(), likeUserId);
            //      4.2.删除redis中的数据
            //      4.3.环信删除好友
            Boolean aBoolean = imTemplate.deleteContact("hx" + UserHolderUtil.getUserId(), "hx" + likeUserId);
            if (!aBoolean) {
                throw  new RuntimeException("系统繁忙");
            }

        }

    }

    // 搜附近
    public List<NearUserVo> queryNearby(String gender, String distance) {
        // 1.调用api 查询附近的用户(返回的是附近的人的所有用户的id
        Long id = UserHolderUtil.getUserId();
        List<Long> userIds = locationApi.listUsersNearby(id, Double.valueOf(distance));
        // 2.判断集合是否为空
        if (CollUtil.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        // 3.调用 userInfoApi 根据用户id查询用户信息

        UserInfo userInfo = new UserInfo();
        userInfo.setGender(gender);
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, userInfo);
        // 4.构造返回
        List<NearUserVo> vos = new ArrayList<>();

        for (Long userId : userIds) {
            if (Objects.equals(userId, id)) continue;
            UserInfo info = map.get(userId);
            if (info != null) {
                NearUserVo vo = NearUserVo.init(info);
                vos.add(vo);
            }
        }
        return vos;
    }


    // 从redis中查询是否双向喜欢的方法
    private Boolean isLike(Long userId, Long likeUserId) {
        String key = Constants.USER_LIKE_KEY + userId;
        return redisTemplate.opsForSet().isMember(key, likeUserId.toString());
    }
}

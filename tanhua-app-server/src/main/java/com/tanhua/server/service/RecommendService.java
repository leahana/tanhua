package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.tanhua.api.QuestionApi;
import com.tanhua.api.RecommendUserApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.autoconfig.template.ImTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:28
 */

@Service
public class RecommendService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @Autowired
    private ImTemplate imTemplate;

    //查询今日佳人
    public TodayBest queryTodayBest() {

        // 1.获取用户id
        Long userId = UserHolderUtil.getUserId();
        // 2.调用api查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);

        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(99d);
        }

        // 2.1 根据 recommendUser 中的用户id 查询推荐用户的id
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        // 3.分装成vo
        TodayBest todayBest = new TodayBest();
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        // 4.返回
        return vo;
    }

    public PageResult queryRecommendationFriends(RecommendUserDto recommendUserDto) {


        // 1.获取用户id
        Long userId = UserHolderUtil.getUserId();

        Integer page = recommendUserDto.getPage();
        Integer pageSize = recommendUserDto.getPageSize();
        // 2.获取分页中的RecommendUsers
        PageResult pageResult = recommendUserApi.queryRecommendUserList(userId, page, pageSize);
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


    public TodayBest queryPersonalInfo(Long userId) {

        // 1.根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(userId);

        // 2.根据操作人id和查看用户的id查询缘分值
        RecommendUser recommendUser = recommendUserApi.queryByUserId(userId, UserHolderUtil.getUserId());

        // 3.构造返回值

        return TodayBest.init(userInfo, recommendUser);
    }

    public String queryQuestions(Long userId) {
        Question question = questionApi.queryQuestionByUserId(userId);
        return question == null ? "你是?" : question.getTxt();
    }

    //回复陌生人问题
    public void replyQuestions(Long userId, String reply) {
        // 1. 构造消息数据
        Long id = UserHolderUtil.getUserId();
        UserInfo userinfo = userInfoApi.findById(id);

        Map map = new HashMap();
        map.put("userId", id);
        map.put("huanxinId", Constants.HX_USER_PREFIX + id);
        map.put("nickname", userinfo.getNickname());
        map.put("strangerQuestion", queryQuestions(userId));
        map.put("reply", reply);

        String messge = JSONObject.toJSONString(map);

        // 2.调用template对象.发送消息
        Boolean aBoolean = imTemplate.sendMsg(
                Constants.HX_USER_PREFIX + userId, messge);

        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }

    }
}

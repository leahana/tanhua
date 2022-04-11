package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.RecommendUserApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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

//        List<Long> ids = recommendUsers.stream()
//                .map(RecommendUser::getUserId).collect(Collectors.toList());

        // 5.构建查询条件,批量查询
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(recommendUserDto.getAge());
        userInfo.setGender(recommendUserDto.getGender());
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        // 6.构建vo对象
        recommendUsers.forEach(item->{
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

}

package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.api.*;
import com.tanhua.autoconfig.template.ImTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.Announcement;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.*;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 13:42
 * @Desc: 消息
 */

@Service
public class MessageService {


    @DubboReference
    private UserApi userApi;


    @DubboReference
    private UserInfoApi userInfoApi;


    @DubboReference
    private FriendApi friendApi;


    @Autowired
    private ImTemplate imTemplate;


    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private AnnouncementApi announcementApi;


    /**
     * 根据 环信id  获取用户信息
     *
     * @param imId 环信id
     * @return userInfoVo
     */
    public UserInfoVo queryUserInfoByIm(String imId) {
        // 1.根据环信 id  获取用户基本信息
        User user = userApi.queryByImId(imId);

        // 2.根据用户id  获取用户详细信息
        UserInfo userInfo = userInfoApi.findById(user.getId());

        // 3.构建UserInfoVo
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        Integer age = userInfo.getAge();
        if (age != null) {
            userInfoVo.setAge(age.toString());
        }
        return userInfoVo;
    }

    /**
     * 根据 添加环信好友方法
     *
     * @param friendId 好友id
     */
    public void addContact(Long friendId) {
        // 1.将好友关系注册到环信
        Boolean aBoolean = imTemplate.addContact(
                Constants.HX_USER_PREFIX + UserHolderUtil.getUserId(),
                Constants.HX_USER_PREFIX + friendId);

        if (!aBoolean) {
            throw new BusinessException(ErrorResult.error());
        }
        // 2.如果注册成功,记录好友关系到mongoDB
        Boolean isSave = friendApi.addFriend(UserHolderUtil.getUserId(), friendId);

    }

    /**
     * 分页查询好友列表
     *
     * @param keyword 关键字
     * @return PageResult
     */
    public PageResult queryFriends(Integer page, Integer pageSize, String keyword) {

        // 1.调用API查询当前用户的好友数据
        List<Friend> friendList = friendApi.queryFriends(UserHolderUtil.getUserId(), page, pageSize, keyword);
        if (CollUtil.isEmpty(friendList)) {
            return new PageResult();
        }

        // 2.提取列表中的好友id
        List<Long> ids = CollUtil.getFieldValues(friendList, "friendId", Long.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(keyword);

        // 3.调用userInfoApi查询好友的详细信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, userInfo);

        // 4 构造vo对象
        List<ContactVo> voList = new ArrayList<>();
        friendList.forEach(friend -> {
            UserInfo info = map.get(friend.getFriendId());
            if (info != null) {
                ContactVo vo = ContactVo.init(info);
                voList.add(vo);
            }
        });

        // 5.构造返回
        return new PageResult(page, pageSize, 0, voList);

    }


    /**
     * 分页查询消息列表
     */
    public PageResult messageCommentList(CommentType type, Integer page, Integer pageSize) {

        // 1. 用户id查询互动的用户id
        List<Comment> comments = commentApi.queryCommentUserIds(UserHolderUtil.getUserId(), type, page, pageSize);
        // 提取互动的用户id
        List<Long> ids = CollUtil.getFieldValues(comments, "userId", Long.class);
        if (CollUtil.isEmpty(ids)) {
            return new PageResult();
        }

        // 2. 根据用户的id查询用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);
        List<MessageLike> vos = new ArrayList<>();

        // 3. 构造vo
        comments.forEach(comment -> {
            UserInfo userInfo = map.get(comment.getUserId());
            if (userInfo != null) {
                Long created = comment.getCreated();
                MessageLike init = MessageLike.init(userInfo);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = sdf.format(new Date(created));
                init.setCreateDate(format);
                vos.add(init);
            }
        });

        // 4. 返回结果
        return new PageResult(page, pageSize, 0, vos);
    }

    /**
     * 分页查询公告列表
     */
    public PageResult queryAnnouncements(Integer page, Integer pageSize) {
        // 1. 查询公告列表(Mysql tb_announcement表)
        IPage<Announcement> iPage
                = announcementApi.queryAnnouncements(page, pageSize);
        if (iPage == null) {
            return new PageResult();
        }

        // 2. 构造vo
        List<Announcement> records = iPage.getRecords();
        List<AnnouncementVo> vos = new ArrayList<>();
        if (CollUtil.isNotEmpty(records)) {
            records.forEach(announcement -> {
                AnnouncementVo vo = AnnouncementVo.init(announcement);
                vos.add(vo);
            });
        }

        // 3. 返回结果
        return new PageResult(page, pageSize, 0, vos);
    }
}

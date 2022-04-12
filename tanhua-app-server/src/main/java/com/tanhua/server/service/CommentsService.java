package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.CommentApi;
import com.tanhua.api.MovementApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.CommentVo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.checkerframework.checker.signature.qual.PolySignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: leah_ana
 * @Date: 2022/4/12 13:29
 */

@Service
@Slf4j
public class CommentsService {

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发布评论
    public void save(String movementId, String content) {
        // 1.获取操作用户id
        Long userId = UserHolderUtil.getUserId();

        // 2.构建Comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        // 3.调用 api 保存数据

        Integer commentCount = commentApi.save(comment);

        log.info("commentCount:{}", commentCount);
    }

    //查询评论列表
    public PageResult queryComments(Integer page, Integer pageSize, String publishId) {
        //  1. 根据id查询动态(publishId 查询 Comment集合
        List<Comment> commentList = commentApi.queryComments(publishId, page, pageSize, CommentType.COMMENT);

        if (commentList == null || commentList.size() == 0) {
            return new PageResult();
        }

        List<Long> ids = CollUtil.getFieldValues(commentList, "userId", Long.class);
//        List<Long> ids = commentList.stream().map(Comment::getUserId).collect(Collectors.toList());
        //  3  根据id查询用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);

        List<CommentVo> vos = new ArrayList<>();
        commentList.forEach(comment -> {
            Long userId = comment.getUserId();
            UserInfo userInfo = map.get(userId);
            CommentVo vo = CommentVo.init(userInfo, comment);
            vos.add(vo);
        });


        return new PageResult(page, pageSize, 0, vos);

    }


    /**
     * 动态 点赞
     */
    public Integer likeComment(String movementId) {
        // 1.查询 用户是否点过赞
        Boolean hasComment = commentApi.hasComment(movementId,
                UserHolderUtil.getUserId(),CommentType.LIKE);

        // 2.如果点过赞 抛出异常
        if (hasComment) {
            throw new BusinessException(ErrorResult.likeError());
        }

        // 3.如果没有点赞调用api 保存数据
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolderUtil.getUserId());
        comment.setCreated(System.currentTimeMillis());

        Integer count = commentApi.save(comment);

        // 4.将点赞状态存入redis 将用户点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;

        String hasKey=Constants.MOVEMENT_LIKE_HASHKEY+UserHolderUtil.getUserId();

        redisTemplate.opsForHash().put(key, hasKey, "1");

        return count;
    }

/*    //查询评论列表
    public PageResult queryComments(Integer page, Integer pageSize, String publishId) {
        //  1. 根据id查询动态(publishId 查询 Comment集合
        List<Comment> commentList = commentApi.queryComments(publishId);

        if (commentList == null || commentList.size() == 0)
            return new PageResult(page, pageSize, 0, null);
        //  2  获取该动态评论用户的id 过滤掉不是评论的互动
        commentList = commentList.stream()
                .filter(comment -> comment.getCommentType() != CommentType.COMMENT.getType()).collect(Collectors.toList());

        List<Long> ids = CollUtil.getFieldValues(commentList, "userId", Long.class);
//        List<Long> ids = commentList.stream().map(Comment::getUserId).collect(Collectors.toList());
        //  3  根据id查询用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);

        List<CommentVo> vos = new ArrayList<>();
        commentList.forEach(comment -> {
            Long userId = comment.getUserId();
            UserInfo userInfo = map.get(userId);
            CommentVo vo = CommentVo.init(userInfo, comment);
            vos.add(vo);
        });


        return new PageResult(page, pageSize, commentList.size(), vos);

    }*/
}

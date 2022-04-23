package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.CommentApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/12 13:29
 * @Desc: 评论
 */

@Service
@Slf4j
public class CommentsService {

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 动态评论 保存
     *
     * @param movementId 动态id
     * @param content    评论内容
     */
    public void saveComment(String movementId, String content) {
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
        Integer commentCount = commentApi.saveComment(comment);

        //日志记录
        log.info("commentCount:{}", commentCount);
    }

    /**
     * 分页查询动态评论列表
     *
     * @param page      页码
     * @param pageSize  每页数量
     * @param publishId 动态id
     * @return 分页结果
     */
    public PageResult pageComments(Integer page, Integer pageSize, String publishId) {
        //  1. 根据id查询动态(publishId 查询 Comment集合
        List<Comment> commentList = commentApi.pageComments(publishId, page, pageSize, CommentType.COMMENT);

        //  2. 判断返回结果
        if (commentList == null || commentList.size() == 0) {
            return new PageResult();
        }
        //  3. 提取动态评论的用户id
        List<Long> ids = CollUtil.getFieldValues(commentList, "userId", Long.class);

//        List<Long> ids = commentList.stream().map(Comment::getUserId).collect(Collectors.toList());

        //  4. 根据id查询用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);

        //  5. 构造vo对象
        List<CommentVo> vos = new ArrayList<>();
        commentList.forEach(comment -> {
            Long userId = comment.getUserId();
            UserInfo userInfo = map.get(userId);
            CommentVo vo = CommentVo.init(userInfo, comment);
            vos.add(vo);
        });

        // 6. 返回结果
        return new PageResult(page, pageSize, 0, vos);

    }

    /**
     * 动态点赞
     *
     * @param movementId 动态id
     * @return 更新点赞结果之后的最新数量
     */
    public Integer likeMovement(String movementId) {

        // 1.查询 用户是否点过赞(从MongoDB数据库中查询comment表)
        Boolean hasComment = commentApi.hasComment(movementId,
                UserHolderUtil.getUserId(), CommentType.LIKE);

        // 2.如果点过赞 抛出异常
        if (hasComment) {
            throw new BusinessException(ErrorResult.likeError());
        }

        // 3.如果没有点赞调用api 保存数据
        //     3.1 构建Comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolderUtil.getUserId());
        comment.setCreated(System.currentTimeMillis());
        //     3.2 调用api 保存数据
        Integer count = commentApi.saveComment(comment);

        // 4.将点赞状态存入redis 将用户点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hasKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolderUtil.getUserId();
        redisTemplate.opsForHash().put(key, hasKey, "1");

        // 5.返回最新的点赞数量
        return count;
    }

    /**
     * 动态取消点赞
     *
     * @param movementId 动态id
     * @return 更新点赞结果之后的最新数量
     */
    public Integer dislikeMovement(String movementId) {

        // 1.查询 用户是否点过赞(从MongoDB数据库中查询comment表)
        Boolean hasComment = commentApi.hasComment(movementId,
                UserHolderUtil.getUserId(), CommentType.LIKE);

        // 2.如果未点赞  抛出异常
        if (!hasComment) {
            throw new BusinessException(ErrorResult.disLikeError());
        }

        // 3.如果点赞了 调用api 删除数 返回点赞数量
        //      3.1 构建Comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setUserId(UserHolderUtil.getUserId());
        //      3.2 调用api删除数据
        Integer count = commentApi.deleteComment(comment);

        // 4.删除 redis中的点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hasKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolderUtil.getUserId();
        redisTemplate.opsForHash().delete(key, hasKey);

        // 5.返回最新的点赞数量
        return count;
    }

    /**
     * 动态喜欢
     *
     * @param movementId 动态id
     * @return 更新喜欢结果之后的最新数量
     */
    public Integer loveMovement(String movementId) {

        // 1.查询 用户是否点过喜欢(从MongoDB数据库中查询comment表)
        Boolean hasComment = commentApi.hasComment(movementId,
                UserHolderUtil.getUserId(), CommentType.LOVE);

        // 2.如果已经喜欢 抛出异常
        if (hasComment) {
            throw new BusinessException(ErrorResult.loveError());
        }

        // 3.如果没有点赞调用api 保存数据
        Comment comment = new Comment();
        //      3.1 构建Comment对象
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolderUtil.getUserId());
        comment.setCreated(System.currentTimeMillis());
        //      3.2 调用api 保存数据
        Integer count = commentApi.saveComment(comment);

        // 4.将点赞状态存入redis 将用户点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hasKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolderUtil.getUserId();
        redisTemplate.opsForHash().put(key, hasKey, "1");

        // 5.返回最新的喜欢数量
        return count;

    }

    /**
     * 动态取消喜欢
     *
     * @param movementId 动态id
     * @return 更新喜欢结果之后的最新数量
     */
    public Integer unloveMovement(String movementId) {

        // 1.查询 用户是否点过喜欢
        Boolean hasComment = commentApi.hasComment(movementId,
                UserHolderUtil.getUserId(), CommentType.LOVE);

        // 2.如果未喜欢 抛出异常
        if (!hasComment) {
            throw new BusinessException(ErrorResult.disloveError());
        }

        // 3.如果点赞了 调用api 删除数 返回点赞数量
        //   3.1 构建Comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setUserId(UserHolderUtil.getUserId());
        //   3.2 调用api删除数据
        Integer count = commentApi.deleteComment(comment);

        // 4.删除 redis中的点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hasKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolderUtil.getUserId();
        redisTemplate.opsForHash().delete(key, hasKey);

        // 5.返回最新的喜欢数量
        return count;
    }

    /**
     * 动态评论 点赞
     *
     * @param commentId 评论id
     * @return 更新点赞结果之后的最新数量
     */
    public Integer likeComment(String commentId) {
        // 1.查询用户是否给这条评论点过赞
        Boolean isThumb = commentApi.checkEvaluate(UserHolderUtil.getUserId(), commentId);

        // 2.如果点过赞  抛出异常
        if (isThumb) {
            throw new BusinessException(ErrorResult.likeError());
        }

        // 3.如果没有点赞调用api 保存数据
        Integer count = commentApi.saveEvaluate(UserHolderUtil.getUserId(), commentId);

        // 4.将点赞状态存入redis 将用户点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + commentId;
        String hashKey = Constants.MOVEMENT_EVALUATE_HASHKEY + UserHolderUtil.getUserId();
        redisTemplate.opsForHash().put(key, hashKey, "1");
        // 5.返回最新的点赞数量
        return count;
    }

    /**
     * 动态评论 取消点赞
     *
     * @param commentId 评论id
     * @return 更新取消点赞结果之后的最新数量
     */
    public Integer dislikeComment(String commentId) {

        // 1.查询用户是否给这条评论点过赞
        Boolean isThumb = commentApi.checkEvaluate(UserHolderUtil.getUserId(), commentId);

        // 2.如果点过赞  抛出异常
        if (!isThumb) {
            throw new BusinessException(ErrorResult.likeError());
        }

        // 3.如果没有点赞调用api 保存数据
        Integer count = commentApi.deleteEvaluate(UserHolderUtil.getUserId(), commentId);

        // 4.将点赞状态存入redis 将用户点赞状态存入redis
        String key = Constants.MOVEMENTS_INTERACT_KEY + commentId;
        String hashKey = Constants.MOVEMENT_EVALUATE_HASHKEY + UserHolderUtil.getUserId();
        redisTemplate.opsForHash().delete(key, hashKey, "1");

        // 5.返回最新的点赞数量
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

package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.MovementApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.api.VisitorsApi;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.*;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: leah_ana
 * @Date: 2022/4/11 15:06
 */

@Service
public class MovementService {

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VisitorsApi visitorsApi;

    @Autowired
    private MqMessageService mqMessageService;


    /**
     * 发布动态
     *
     * @param movement     动态文字内容
     * @param imageContent 动态图片数组
     */
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {

        // 1.判断发布动态的内容是否存在
        if (StringUtils.isEmpty(movement.getTextContent()) && imageContent.length == 0)
            throw new BusinessException(ErrorResult.contentError());

        // 2.获取当前登录的用户id
        Long userId = UserHolderUtil.getUserId();

        // 3.将文件内容上传到阿里云Oss
        List<String> imageUrlList = new ArrayList<>();
        for (MultipartFile multipartFile : imageContent) {
            String url = ossTemplate.upload(multipartFile.getOriginalFilename()
                    , multipartFile.getInputStream());
            imageUrlList.add(url);
        }

        // 4.将对象封装到Movement中
        movement.setUserId(userId);
        movement.setMedias(imageUrlList);

        // 5.调用api完成发布动态
        String momentId = movementApi.publishMovement(movement);

        // 6.将动态id发送到RabbitMQ中
        mqMessageService.sendAudiMessage(momentId);
    }

    /**
     * 查询个人动态
     *
     * @param userId   用户id
     * @param page     页码
     * @param pageSize 每页数量
     * @return pageResult
     */
    public PageResult pageMovements(Long userId, Integer page, Integer pageSize) {

        // 1.根据 用户id查询 个人动态 mongoDB (movement)
        List<Movement> movements = movementApi.listMovements(userId, page, pageSize);

        // 3.判断
        if (movements == null || movements.size() == 0) {
            return new PageResult(page, pageSize, 0, movements);
        }

        // 4.循环数据列表,构建vo
        //先获取用户详情
        UserInfo userInfo = userInfoApi.findById(userId);
        List<MovementsVo> list = new ArrayList<>();
        movements.forEach(movement -> {
            MovementsVo init = MovementsVo.init(userInfo, movement);
            list.add(init);
        });

        // 5.构建返回值
        return new PageResult(page, pageSize, movements.size(), list);
    }

    /**
     * 查询好友动态
     *
     * @param page     页码
     * @param pageSize 分页大小
     * @return PageResult
     */
    public PageResult pageFriendsMovement(Integer page, Integer pageSize) {

        // 1.查询好友动态详情数据
        Long userId = UserHolderUtil.getUserId();

        // 2.好友动态数据
        List<Movement> movements = movementApi.listFriendsMovements(userId, page, pageSize);

        // 3.返回结果
        return getPageResult(page, pageSize, movements);
    }

    /**
     * 查询推荐用户动态
     *
     * @param page     页码
     * @param pageSize 分页大小
     * @return PageResult
     */
    public PageResult pageRecommendMovements(Integer page, Integer pageSize) {

        // 1.从redis中获取推荐数据
        String redisKey = Constants.MOVEMENTS_RECOMMEND + UserHolderUtil.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        // 2.判断推荐数据是否存在
        //List<Movement> movementList = Collections.EMPTY_LIST;
        List<Movement> movementList = new ArrayList<>();
        if (StringUtils.isEmpty(redisValue)) {
            // 3.如果不存在 调用api随机生成10条数据
            movementList = movementApi.randomMovements(pageSize);
        } else {
            //"16,17,18,19,10100,10101,10102,10130"
            // 4.如果存在处理pid数据
            String[] split = redisValue.split(",");
            // 判断当前页的起始条数是否小于数组总数
            if ((page - 1) * pageSize < split.length) {
                List<Long> pids = Arrays.stream(split).skip((long) (page - 1) * pageSize).limit(pageSize)
                        .map(Long::parseLong).collect(Collectors.toList());
        // 5.根据pid数组查询用户数据
                movementList = movementApi.listMovementsByPids(pids);
            }
        }
        // 6.构建返回值
        return getPageResult(page, pageSize, movementList);
    }

    /**
     * 查询动态详情
     *
     * @param movementId 动态id
     * @return 动态详情
     */
    public MovementsVo getMovement(String movementId) {
        // 调用消息服务 发送日志信息到RabbitMQ

        mqMessageService.sendLogMessage(UserHolderUtil.getUserId(),"0202","movement",movementId);
        // 1. 根据movementId查询动态详情
        Movement movement = movementApi.getMovement(movementId);
        // 2. 根据userId查询用户详情
        if (movement != null) {
            Long userId = movement.getUserId();
            UserInfo userInfo = userInfoApi.findById(userId);
            // 3. 构建返回值
            // 4. 返回
            return MovementsVo.init(userInfo, movement);
        } else {
            return null;
        }
    }

    /**
     * 近日访客
     * */
    public List<VisitorsVo> listVisitors() {
        // 1. 查询访问时间
        String key = Constants.VISITORS_USER;
        String hashKey = String.valueOf(UserHolderUtil.getUserId());

        String value = (String) redisTemplate.opsForHash().get(key, hashKey);
        Long date = StringUtils.isEmpty(value) ? null : Long.parseLong(value);

        // 2.查询访客列表
        List<Visitors> list = visitorsApi.listVisitors(date, UserHolderUtil.getUserId());
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }

        // 3.提取用户id 查看用户详情
        List<Long> ids = CollUtil.getFieldValues(list, "visitorUserId", Long.class);

        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);
        // 4. 构建vo返回
        List<VisitorsVo> vos = new ArrayList<>();
        for (Visitors visitors : list) {
            UserInfo userInfo = map.get(visitors.getVisitorUserId());
            if (userInfo != null) {
                VisitorsVo vo = VisitorsVo.init(userInfo, visitors);
                vos.add(vo);
            }
        }
        return vos;
    }

    //封装PageResult 好友动态通用动态的公用方法
    private PageResult getPageResult(Integer page, Integer pageSize, List<Movement> movements) {
        if (CollUtil.isEmpty(movements)) return new PageResult();
        //if (movements == null || movements.isEmpty()) return pageResult;
        // 2. 提取动态发布人id
        List<Long> ids = CollUtil.getFieldValues(movements, "userId", Long.class);

        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);

        List<MovementsVo> list = new ArrayList<>();

        // 2.查询好友动态发布人
        movements.forEach(movement -> {
            UserInfo userInfo = map.get(movement.getUserId());

            // 3.构造vo对象
            if (userInfo != null) {
                MovementsVo movementsVo = MovementsVo.init(userInfo, movement);

                String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();

                //修复点赞状态bug(从redis中获取点赞状态)
                String hasKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolderUtil.getUserId();
                if (redisTemplate.opsForHash().hasKey(key, hasKey)) {
                    movementsVo.setHasLiked(1);
                }

                //修复喜欢状态bug(从redis中获取喜欢状态)
                String lovHasKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolderUtil.getUserId();
                if (redisTemplate.opsForHash().hasKey(key, lovHasKey)) {
                    movementsVo.setHasLoved(1);
                }


                list.add(movementsVo);
            }
        });

        return new PageResult(page, pageSize, 0, list);
    }

}
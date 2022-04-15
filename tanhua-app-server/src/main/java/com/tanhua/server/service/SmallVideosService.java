package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.api.FocusUserApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.api.VideoApi;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 20:43
 */

@Service
public class SmallVideosService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private FocusUserApi FocusUserApi;

    //上传视频
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        if (videoThumbnail.isEmpty() || videoFile.isEmpty()) {
            throw new BusinessException(ErrorResult.error());
        }
        // 1.将视频上传到FastDFS
        String filename = videoFile.getOriginalFilename();
        assert filename != null;
        filename = filename.substring(filename.lastIndexOf(".") + 1);
        StorePath storePath = fastFileStorageClient.uploadFile(
                videoFile.getInputStream(),
                videoFile.getSize(),
                filename, null);
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
        // 2.将视频图片上传到阿里云oss
        String imageUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        // 3.构建Videos对象
        Video video = new Video();
        video.setUserId(UserHolderUtil.getUserId());
        video.setPicUrl(imageUrl);
        video.setVideoUrl(videoUrl);
        video.setText("我就是个寄");
        // 4.调用API保存数据
        String videoId = videoApi.save(video);

        if (StringUtils.isEmpty(videoId)) {
            throw new BusinessException(ErrorResult.error());
        }
    }


    //获取视频列表
    public PageResult queryVideoList(Integer page, Integer pageSize) {
        // 1.查询redis
        String redisKey = Constants.VIDEOS_RECOMMEND + UserHolderUtil.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        // 2.判断redis数据是否存在 判断redis中的数据是否满足本次分页条件
        List<Video> list = new ArrayList<>();
        int redisPage = 0;
        if (!StringUtils.isEmpty(redisValue)) {
            // 3.如果redis数据存在,根据VID 查询数据
            String[] split = redisValue.split(",");
            // 判断当前页的起始条数是否小于数组总数
            if ((page - 1) * pageSize < split.length) {
                List<Long> vids = Arrays.stream(split).skip((long) (page - 1) * pageSize).limit(pageSize)
                        .map(Long::parseLong).collect(Collectors.toList());
                list = videoApi.queryMovementsByVids(vids);
            }
            redisPage = PageUtil.totalPage(split.length, pageSize);
        }

        // 4.如果redis 数据不存在 分页查询视频数据
        if (CollUtil.isEmpty(list)) {
            //page计算  传入的页码 -redis中查询的总页数

            list = videoApi.queryVideos(page - redisPage, pageSize);
        }
        // 5.提取视频列表中所有的用户id
        List<Long> ids = CollUtil.getFieldValues(list, "userId", Long.class);
        // 6.查询用户信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);

        //  查询用户是否关注


        // 7.构建返回值
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo userInfo = map.get(video.getUserId());
            if (userInfo != null) {
                VideoVo vo = VideoVo.init(userInfo, video);
                String FOCUS_USER_KEY = Constants.FOCUS_USER_KEY + UserHolderUtil.getUserId();
                String hashKey = userInfo.getId().toString();
                Boolean aBoolean = redisTemplate.opsForHash().hasKey(FOCUS_USER_KEY, hashKey);
                vo.setHasFocus(aBoolean ? 1 : 0);
                String VIDEO_LIKE_KEY = Constants.VIDEO_LIKE + UserHolderUtil.getUserId();
                hashKey = Constants.VIDEO_LIKE_HASHKEY + video.getId();
                Boolean isLike = redisTemplate.opsForHash().hasKey(VIDEO_LIKE_KEY, hashKey);
                vo.setHasLiked(isLike ? 1 : 0);
                vos.add(vo);
            }
        }

        return new PageResult(page, pageSize, 0, vos);
    }

    //关注
    public void addUserFocus(Long uid) {
        String key = Constants.FOCUS_USER_KEY + UserHolderUtil.getUserId();
        String hashKey = uid.toString();
        Boolean isExist = FocusUserApi.checkUserFocus(UserHolderUtil.getUserId(), uid);
        if (isExist) {
            throw  new BusinessException(ErrorResult.error());
        }
        redisTemplate.opsForHash().put(key, hashKey, "1");
        long count = FocusUserApi.upsert(UserHolderUtil.getUserId(), uid, true);
        if (StringUtils.isEmpty(count)) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    //取消关注
    public void deleteUserFocus(Long uid) {
        String key = Constants.FOCUS_USER_KEY + UserHolderUtil.getUserId();
        String hashKey = uid.toString();
        redisTemplate.opsForHash().delete(key, hashKey);
        long delete = FocusUserApi.upsert(UserHolderUtil.getUserId(), uid, false);
    }

    //点赞
    public void addLike(String videoId) {
        // 查询用户是否点过赞
        String key = Constants.VIDEO_LIKE + UserHolderUtil.getUserId();
        String hashKey = Constants.VIDEO_LIKE_HASHKEY + videoId;
        Boolean isExist = videoApi.checkVideoLike(UserHolderUtil.getUserId(), videoId);
        if (isExist) {
            throw new BusinessException(ErrorResult.error());
        }
        redisTemplate.opsForHash().put(key, hashKey, "1");
        long count = videoApi.upsert(UserHolderUtil.getUserId(), videoId, true);
        if (StringUtils.isEmpty(count)) {
            throw new BusinessException(ErrorResult.error());
        }

    }

    //取消点赞
    public void deleteLike(String videoId) {
        String key = Constants.VIDEO_LIKE + UserHolderUtil.getUserId();
        String hashKey = Constants.VIDEO_LIKE_HASHKEY + videoId;
        redisTemplate.opsForHash().delete(key, hashKey);
        long upsert = videoApi.upsert(UserHolderUtil.getUserId(), videoId, false);

    }
}

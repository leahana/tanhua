package com.tanhua.server.service;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.api.SoundApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Sound;
import com.tanhua.model.vo.SoundVo;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: leah_ana
 * @Date: 2022/4/19 19:38
 */

@Service
public class PeachblossomService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @DubboReference
    private SoundApi soundApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveSound(MultipartFile soundFile) throws IOException {
        if (!soundFile.isEmpty()) {
            String filename = soundFile.getOriginalFilename();
            assert filename != null;
            filename = filename.substring(filename.lastIndexOf(".") + 1);
            StorePath storePath = fastFileStorageClient.uploadFile(soundFile.getInputStream()
                    , soundFile.getSize(), filename, null);
            String soundUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
            String s = soundApi.saveSound(soundUrl, UserHolderUtil.getUserId());

            System.err.println(s);
            System.err.println("上传成功" + soundUrl);
        }
    }

    public SoundVo getSound() {
        // 1.从redis中判断是否有今日桃花传音数据
        String key = "sound_count" + UserHolderUtil.getUserId();
        //  如果今日没有使用桃花传音,则初始化redis中的数据 (每天清空一次)
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, "0", 1, TimeUnit.DAYS);
        }
        int redisValue = Integer.parseInt(Objects.requireNonNull(redisTemplate.opsForValue().get(key)));
        if (10 == redisValue) {
            throw new RuntimeException("今日桃花传音获取数量已达上限");
        }
        // 2.如果没有，从数据库中查询先从数据库随机查询十条数据存入redis然后从redis中取SoundId 消耗就抛异常
        Sound sound = soundApi.randomSound(UserHolderUtil.getUserId());
        if (sound != null) {
            UserInfo userInfo = userInfoApi.findById(sound.getUserId());
            if (userInfo != null) {
                SoundVo vo = SoundVo.init(userInfo, sound);
                vo.setRemainingTimes(10 - redisValue);
                redisTemplate.opsForValue().set(key, String.valueOf(redisValue + 1),1, TimeUnit.DAYS);
                System.err.println();
                return vo;
            }
        }
        return new SoundVo();
    }
}

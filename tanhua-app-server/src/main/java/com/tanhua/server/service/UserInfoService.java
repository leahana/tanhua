package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.annotations.Until;
import com.tanhua.api.UserInfoApi;
import com.tanhua.autoconfig.template.ApiFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 19:57
 */

@Service
public class UserInfoService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private ApiFaceTemplate apiFaceTemplate;

    //更新用户信息
    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    //更新用户头像
    public void updateHead(MultipartFile headPhoto, Long id) throws IOException {
        // 1.上传文件到阿里云Oss
        String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        // 2.调用ApiFace图片识别
        boolean detectFace = apiFaceTemplate.detectFace(imageUrl);

        if (!detectFace) {
            // 2.1 不包含人脸 抛出异常
//          throw new RuntimeException("图片中不包含人脸");
            throw new BusinessException(ErrorResult.faceError());
        } else {
            // 2.2 包含人脸 调用Api更新用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setAvatar(imageUrl);
            userInfoApi.update(userInfo);
        }
    }

    //根据id查询
    public UserInfoVo findById(Long id) {
        UserInfo userInfo = userInfoApi.findById(id);

        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        Integer age = userInfo.getAge();
        if (age != null) {
            userInfoVo.setAge(age.toString());
        }
        return userInfoVo;
    }


    //更新用户信息
    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }

    //更新用户头像
    public void updateHeader(MultipartFile headPhoto, Long userId) throws IOException {
        // 1.上传文件到阿里云Oss
        String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        // 2.调用ApiFace图片识别
        boolean detectFace = apiFaceTemplate.detectFace(imageUrl);
        boolean empty = StringUtils.isEmpty(imageUrl);
        if (!detectFace && !empty) {
            // 2.1 不包含人脸 抛出异常
//          throw new RuntimeException("图片中不包含人脸");
            throw new BusinessException(ErrorResult.faceError());
        } else {

            userInfoApi.updateHeader(imageUrl, userId);
        }
    }
}

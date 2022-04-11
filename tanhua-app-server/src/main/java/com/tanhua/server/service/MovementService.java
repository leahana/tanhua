package com.tanhua.server.service;

import com.tanhua.api.MovementApi;
import com.tanhua.api.UserInfoApi;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/11 15:06
 */

@Service
public class MovementService {

    @Autowired
    private OssTemplate ossTemplate;


    @DubboReference
    private MovementApi movementApi;


    @DubboReference
    private UserInfoApi userInfoApi;


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

        movementApi.publishMovement(movement);
    }

    /**
     * 查询个人动态
     *
     * @param userId   用户id
     * @param page     页码
     * @param pageSize 每页数量
     * @return pageResult
     */
    public PageResult queryMovementsByUserId(Long userId, Integer page, Integer pageSize) {

        // 1.根据 用户id查询 个人动态 mongoDB (movement)
        PageResult pageResult = movementApi.queryMovementsByUserId(userId, page, pageSize);

        // 2.获取PageResult中的列表
        List<Movement> items = (List<Movement>) pageResult.getItems();

        // 3.判断
        if (items == null || items.size() == 0) {
            return pageResult;
        }

        // 4.循环数据列表,构建vo
        //先获取用户详情
        UserInfo userInfo = userInfoApi.findById(userId);
        List<MovementsVo> list = new ArrayList<>();
        items.forEach(movement -> {
            MovementsVo init = MovementsVo.init(userInfo, movement);
            list.add(init);
        });
        pageResult.setItems(list);

        // 5.构建返回值
        return pageResult;
    }
}

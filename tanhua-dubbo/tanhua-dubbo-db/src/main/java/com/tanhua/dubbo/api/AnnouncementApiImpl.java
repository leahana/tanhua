package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.api.AnnouncementApi;
import com.tanhua.dubbo.mappers.AnnouncementMapper;
import com.tanhua.model.domain.Announcement;
import com.tanhua.model.domain.BasePojo;
import com.tanhua.model.domain.BlackList;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 19:30
 */

@DubboService
public class AnnouncementApiImpl implements AnnouncementApi {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public IPage<Announcement> queryAnnouncements(Integer page, Integer pageSize) {


        LambdaQueryWrapper<Announcement> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(BasePojo::getUpdated);

        return announcementMapper.selectPage(new Page<>(page, pageSize), lqw);
    }
}

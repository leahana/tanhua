package com.tanhua.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.Announcement;

import java.util.List;

public interface AnnouncementApi {

    //分页查询公告
    IPage<Announcement> pageAnnouncements(Integer page, Integer pageSize);

}

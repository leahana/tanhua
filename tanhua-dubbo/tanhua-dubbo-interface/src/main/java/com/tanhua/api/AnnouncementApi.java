package com.tanhua.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.Announcement;

import java.util.List;

public interface AnnouncementApi {

    IPage<Announcement> queryAnnouncements(Integer page, Integer pageSize);

}

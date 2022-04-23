package com.tanhua.api;

import com.tanhua.model.vo.Visitors;

import java.util.List;

public interface VisitorsApi {

    //保存访客记录
    void saveVisitors(Visitors visitors);

    //获取访客列表
    List<Visitors> listVisitors(Long date, Long userId);

    //分页获取访客列表
    List<Visitors> pageVisitors(Long userId, Integer page, Integer pageSize);
}

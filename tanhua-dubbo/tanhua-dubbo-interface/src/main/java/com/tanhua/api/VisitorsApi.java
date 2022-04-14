package com.tanhua.api;

import com.tanhua.model.vo.Visitors;

import java.util.List;

public interface VisitorsApi {
    void save(Visitors visitors);

    List<Visitors> queryVisitors(Long date, Long userId);
}

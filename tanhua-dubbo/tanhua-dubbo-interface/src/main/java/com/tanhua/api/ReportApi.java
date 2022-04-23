package com.tanhua.api;

import com.tanhua.model.domain.Report;

import java.util.List;

public interface ReportApi {

    // 查询报告id
    Long selectReport(String id, Long userId);

    // 保存报告
    Long saveReport(Long testPaperId, Long userId, Integer isLock);

    // 更新报告
    void updateReport(Long userId, Long testPaperId, Integer score);

    // 获取报告
    Report getReport(String reportId, Long userId);
}

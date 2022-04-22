package com.tanhua.api;

import com.tanhua.model.domain.Report;

import java.util.List;

public interface ReportApi {

    Long selectReport(String id, Long userId);


    Long saveReport(Long testPaperId, Long userId, Integer isLock);

    void updateReport(Long userId, Long testPaperId, Integer score);

    Report findReportById(String reportId, Long userId);
}

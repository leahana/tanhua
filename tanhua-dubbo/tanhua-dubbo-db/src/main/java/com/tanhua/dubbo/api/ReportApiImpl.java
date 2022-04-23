package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tanhua.api.ReportApi;
import com.tanhua.dubbo.mappers.ReportMapper;
import com.tanhua.model.domain.Report;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 15:15
 */

@DubboService
public class ReportApiImpl implements ReportApi {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public Long selectReport(String id, Long userId) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getTestPaperId, id).eq(Report::getUserId, userId);
        Report report = reportMapper.selectOne(wrapper);
        return report == null ? null : report.getId();
    }

    @Override
    public Long saveReport(Long testPaperId, Long userId, Integer isLock) {
        // 在数据库新建报告
        Long aLong = selectReport(testPaperId.toString(), userId);
        if (aLong == null) {
            Report report = new Report();
            report.setUserId(userId);
            report.setTestPaperId(testPaperId);
            report.setScore(0);
            report.setIsLock(isLock);
            reportMapper.insert(report);
            return report.getId();
        } else {
            return aLong;
        }
    }

    @Override
    public void updateReport(Long userId, Long testPaperId, Integer score) {
        // 更新报告
        LambdaUpdateWrapper<Report> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Report::getUserId, userId).eq(Report::getTestPaperId, testPaperId);
        Report report = new Report();
        report.setScore(score);
        //解锁
        report.setIsLock(0);
        reportMapper.update(report, wrapper);
    }

    @Override
    public Report getReport(String reportId, Long userId) {
        LambdaUpdateWrapper<Report> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Report::getId, reportId).eq(Report::getUserId, userId);
        return reportMapper.selectOne(wrapper);
    }
}

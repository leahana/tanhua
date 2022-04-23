package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.commons.utils.DateUtils;
import com.tanhua.model.domain.Analysis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: leah_ana
 * @Date: 2022/4/16 16:13
 * @Desc: 日志分析
 */
@Service
public class AnalysisService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private AnalysisMapper analysisMapper;


    /**
     * 定时统计 tb_log表中的数据 保存或者更新tb_analysis表中的数据
     * 1 查询tb_log表中的数据 注册用户 活跃用户 今日留存用户
     * -- 查询登录次数
     * SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE type = '0101' AND log_time = '2020-09-16'
     * -- 查询注册人数
     * SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE type = '0102' AND log_time = '2020-09-16'
     * -- 查询当日活跃人数
     * SELECT COUNT(DISTINCT user_id) FROM tb_log  WHERE log_time = '2020-09-16'
     * -- 次日留存
     * SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time = '2020-09-16' AND user_id in (
     * SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE type = '0102' AND log_time = '2020-09-15')
     * <p>
     * 2. 构造Analysis对象
     * 3. 保存或者更新tb_analysis表中的数据
     */
    //方便后台统计数据, 定时更新当日信息
    public void analysis() throws ParseException {
        // 1.定义查询日期
        String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
        // 2.统计数据-注册数量
        Integer regCount = logMapper.queryByTypeAndLogTime("0102", todayStr);
        // 3.统计数据-登录数量
        Integer loginCount = logMapper.queryByTypeAndLogTime("0101", todayStr);
        // 4.统计数据-活跃数量
        Integer activeCount = logMapper.queryByLogTime(todayStr);
        // 5.统计数据-次日留存数量
        Integer numRetention1d = logMapper.queryNumRetention1d(todayStr, yesterdayStr);
        // 6.构造Analysis对象

        // 7.根据日期查询数据
        QueryWrapper<Analysis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_date", new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
        Analysis analysis = analysisMapper.selectOne(queryWrapper);
        // 8 如果存在 更新,如果不存在 保存

        if (analysis != null) {
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setUpdated(new Date());
            analysisMapper.updateById(analysis);
        }else {
            analysis = new Analysis();
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(numRetention1d);
            analysis.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(todayStr));
            analysis.setCreated(new Date());
            analysis.setUpdated(new Date());
            analysisMapper.insert(analysis);
        }


    }
}

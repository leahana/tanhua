package com.tanhua.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.model.domain.Analysis;
import com.tanhua.model.domain.Log;
import com.tanhua.model.vo.DateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: leah_ana
 * @Date: 2022/4/20 15:20
 */
@Service
public class DashboardService {

    @Autowired
    private AnalysisMapper analysisMapper;

    public Map getUsersCounts(Map map) {
        String type = (String) map.get("type");
        //101 新增 102 活跃用户 103 次日留存率
        Long sd = Long.parseLong(map.get("sd").toString());
        Long ed = Long.parseLong(map.get("ed").toString());

        if (sd == 0L && ed == 0L) {
            throw new RuntimeException("时间不能为空");
        }

        Date sdd = new Date(sd);
        Date edd = new Date(ed);
        //今年开始时间
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(sdd);
        System.err.println("今年开始时间" + startCalendar.getTime());

        //今年结束时间
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(edd);
        Date edTime = endCalendar.getTime();
        System.err.println("今年结束时间" + edTime);
        // 今年统计数据
        List<DateVo> thisYear = getList(startCalendar, edTime, type);

        startCalendar.setTime(sdd);
        startCalendar.add(Calendar.YEAR, -1);
        endCalendar.add(Calendar.YEAR, -1);
        edTime = endCalendar.getTime();
        // 去年统计数据
        List<DateVo> lastYear = getList(startCalendar, edTime, type);


        Map resMap = new HashMap();
        resMap.put("thisYear", thisYear);
        resMap.put("lastYear", lastYear);

        return resMap;

    }

    // 获取时间段VO数据
    private List<DateVo> getList(Calendar startCalendar, Date edTime, String type) {
        // 变化时间\\
        List<DateVo> vo = new ArrayList<>();
        //获取起始时间的本月的最后一天
        while (true) {
            Date sdTime = startCalendar.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdTime);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            long lastDayOfMonth = calendar.getTime().getTime();
            // 看看开始时间的当月最后一天是否小于结束时间
            if (lastDayOfMonth < edTime.getTime()) {
                // 如果本月最后一天小于结束时间.先计算开始时间到月末最后一天,再设置起始时间为下个月的第一天 再次循环
                long start = startCalendar.getTime().getTime();
                long end = lastDayOfMonth;
                // 获取统计数据
                Long count = getCounts(start, end, type);
                // 封装数据
                DateVo dateVo = new DateVo();
                dateVo.setTitle(startCalendar.getTime().getMonth() + 1+ "");
                dateVo.setAmount(count);
                vo.add(dateVo);
                // 设置开始时间为下个月的第一天
                startCalendar.setTime(new Date(lastDayOfMonth));
                startCalendar.add(Calendar.DATE, 1);
            } else if (lastDayOfMonth > edTime.getTime()) {
                // 若果本月最后一天大于结束时间,说明结束时间和开始时间在同一个月 不需要添加月份只计算开始-结束的日期
                long start = startCalendar.getTime().getTime();
                long end = edTime.getTime();
                // 获取统计数据
                Long count = getCounts(start, end, type);
                // 封装数据
                DateVo dateVo = new DateVo();
                dateVo.setTitle(startCalendar.getTime().getMonth() + 1+"");
                dateVo.setAmount(count);
                vo.add(dateVo);
                break;
            }
        }
        System.err.println(vo);
        return vo;

    }

    // 查询注册人数


    private Long getCounts(long start, long end, String type) {
        // 根据type设置查询字段
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

        //
        //101 新增 102 活跃用户 103 次日留存率
        if (!StringUtils.isEmpty(type)) {
            if (type.equals("101")) {
                //101 查询新增人数
                LambdaQueryWrapper<Analysis> wrapper = new LambdaQueryWrapper<>();
                Date startDate = new Date(start);
                Date endDate = new Date(end);

                wrapper.between(Analysis::getRecordDate, startDate, endDate);
                List<Analysis> analyses = analysisMapper.selectList(wrapper);
                Integer count = 0;
                AtomicReference<Integer> ac = new AtomicReference<>(count);
                if (analyses.size() > 0) {
                    analyses.forEach(temp -> {
                        ac.getAndSet(ac.get() + temp.getNumRegistered());
                    });
                }
                return ac.get().longValue();
            } else if (type.equals("102")) {
                //102 查询活跃用户
                LambdaQueryWrapper<Analysis> wrapper = new LambdaQueryWrapper<>();
                Date startDate = new Date(start);
                Date endDate = new Date(end);
                wrapper.between(Analysis::getRecordDate, startDate, endDate);
                List<Analysis> analyses = analysisMapper.selectList(wrapper);
                Integer count = 0;
                AtomicReference<Integer> ac = new AtomicReference<>(count);
                if (analyses.size() > 0) {
                    analyses.forEach(temp -> {
                        ac.getAndSet(ac.get() + temp.getNumActive());
                    });
                }
                return ac.get().longValue();

            } else if (type.equals("103")) {
                //103 查询次日留存率
                // 根据次日留存计算留存率
                // 递归调用新增人数和活跃用户然后相除如果都为零返回零不相除
                Long registerCounts = getCounts(start, end, "101");
                Long activeCounts = getCounts(start, end, "102");
                if (registerCounts == 0) {
                    return 0L;
                }
                System.err.println("activeCounts:" + activeCounts+"registerCounts:" + registerCounts);
                return activeCounts / registerCounts;
            } else {
                return 0L;
            }
        }
        return 0L;
    }
}

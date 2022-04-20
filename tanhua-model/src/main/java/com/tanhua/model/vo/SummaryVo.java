package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryVo {

    //累计用户
    private Integer cumulativeUsers;

    //过去30天活跃用户
    private Integer activePassMonth;

    //过去7天活跃用户
    private Integer activePassWeek;

    //今日新增用户
    private Integer newUsersToday;

    //今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
    private Integer newUsersTodayRate;

    //今日登录次数
    private Integer loginTimesToday;

    //今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
    private Integer loginTimesTodayRate;

    //今日活跃用户
    private Integer activeUsersToday;

    //今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
    private Integer activeUsersTodayRate;


}

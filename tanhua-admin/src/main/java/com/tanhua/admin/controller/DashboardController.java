package com.tanhua.admin.controller;

import com.tanhua.admin.service.DashboardService;
import com.tanhua.model.vo.SummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/20 15:16
 * @Desc: 报表
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 统计用户数量
     */
    @GetMapping("/users")
    public ResponseEntity getStatistics (@RequestParam Map map) {
        Map reqMap = dashboardService.getStatistics(map);
        return ResponseEntity.ok(reqMap);
    }

    /**
     * 总结报表
     */
    @GetMapping("/summary")
    public ResponseEntity getSummary() {
        SummaryVo vo = dashboardService.getSummary();
        return ResponseEntity.ok(vo);
    }
}

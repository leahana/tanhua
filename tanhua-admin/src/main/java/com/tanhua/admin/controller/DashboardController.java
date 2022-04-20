package com.tanhua.admin.controller;

import com.tanhua.admin.service.DashboardService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/20 15:16
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;


    @GetMapping("/users")
    public ResponseEntity getUsersCounts(@RequestParam Map map) {
        Map reqMap = dashboardService.getUsersCounts(map);

        return ResponseEntity.ok(reqMap);
    }
}

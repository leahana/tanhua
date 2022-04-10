package com.tanhua.server.controller;

import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.BeautyTodayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:32
 */

@RestController
@RequestMapping("/tanhua")
public class BeautyTodayController {

    @Autowired
    private BeautyTodayService beautyTodayService;

    @GetMapping("/todayBest")
    public ResponseEntity queryTodayBest() {
        TodayBest todayBest = beautyTodayService.queryTodayBest();
        return ResponseEntity.ok(todayBest);
    }
}

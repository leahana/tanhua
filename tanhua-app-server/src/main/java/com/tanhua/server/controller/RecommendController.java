package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:32
 */

@RestController
@RequestMapping("/tanhua")
public class RecommendController {

    @Autowired
    private RecommendService beautyTodayService;

    @GetMapping("/todayBest")
    public ResponseEntity queryTodayBest() {
        TodayBest todayBest = beautyTodayService.queryTodayBest();
        return ResponseEntity.ok(todayBest);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<PageResult> queryRecommendationFriends( RecommendUserDto recommendUserDto) {

        PageResult pageResult = beautyTodayService.queryRecommendationFriends(recommendUserDto);
        return ResponseEntity.ok(pageResult);
    }
}

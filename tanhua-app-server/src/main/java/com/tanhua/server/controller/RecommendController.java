package com.tanhua.server.controller;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.QuestionApi;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.RecommendService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 19:32
 */

@RestController
@RequestMapping("/tanhua")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/todayBest")
    public ResponseEntity queryTodayBest() {
        TodayBest todayBest = recommendService.queryTodayBest();

        return ResponseEntity.ok(todayBest);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<PageResult> queryRecommendationFriends(RecommendUserDto recommendUserDto) {

        PageResult pageResult = recommendService.queryRecommendationFriends(recommendUserDto);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查看佳人信息
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity queryPersonalInfo(@PathVariable("id") Long userId) {
        TodayBest todayBest = recommendService.queryPersonalInfo(userId);
        return ResponseEntity.ok(todayBest);
    }

    /**
     * 查看陌生人问题
     *
     * @param userId 用户id
     * @return 问题列表
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity queryQuestions(Long userId) {

        String questions = recommendService.queryQuestions(userId);

        return ResponseEntity.ok(questions);
    }

    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map) {

        String _userId = map.get("userId").toString();
        Long userId = Long.parseLong(_userId);
        String reply = map.get("reply").toString();


        recommendService.replyQuestions(userId,reply);

        return ResponseEntity.ok(null);
    }

    /**
     * 探花-推荐用户列表
     */
    @GetMapping("/cards")
    public ResponseEntity queryCardsList() {
        List<TodayBest> list = this.recommendService.queryCardsList();
        return ResponseEntity.ok(list);
    }


    /**
     * 喜欢
     */
    @GetMapping("{id}/love")
    public ResponseEntity<Void> likeUser(@PathVariable("id") Long likeUserId) {
        this.recommendService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 不喜欢
     */
    @GetMapping("{id}/unlove")
    public ResponseEntity<Void> dislikeUser(@PathVariable("id") Long likeUserId) {
        this.recommendService.dislikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }



}

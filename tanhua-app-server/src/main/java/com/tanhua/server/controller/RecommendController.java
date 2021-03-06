package com.tanhua.server.controller;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.api.QuestionApi;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.NearUserVo;
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
 * @Desc: 推荐
 */

@RestController
@RequestMapping("/tanhua")
public class RecommendController {


    @Autowired
    private RecommendService recommendService;

    @GetMapping("/todayBest")
    public ResponseEntity getTodayBest() {
        TodayBest todayBest = recommendService.getTodayBest();

        return ResponseEntity.ok(todayBest);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<PageResult> listRecommendationFriends(RecommendUserDto recommendUserDto) {

        PageResult pageResult = recommendService.listRecommendationFriends(recommendUserDto);

        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查看佳人信息
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity getPersonalInfo(@PathVariable("id") Long userId) {
        TodayBest todayBest = recommendService.getPersonalInfo(userId);
        return ResponseEntity.ok(todayBest);
    }

    /**
     * 查看陌生人问题
     *
     * @param userId 用户id
     * @return 问题列表
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity listQuestions(Long userId) {

        String questions = recommendService.listQuestions(userId);

        return ResponseEntity.ok(questions);
    }

    /**
     * 回复陌生人问题
     *
     * @param map userId:用户id ,reply:回答
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map) {

        String _userId = map.get("userId").toString();
        Long userId = Long.parseLong(_userId);
        String reply = map.get("reply").toString();

        recommendService.replyQuestions(userId, reply);

        return ResponseEntity.ok(null);
    }


    /**
     * 探花-推荐用户列表(小卡片
     */
    @GetMapping("/cards")
    public ResponseEntity listRecommends() {
        List<TodayBest> list = recommendService.listRecommends();
        return ResponseEntity.ok(list);
    }

    /**
     * 喜欢
     */
    @GetMapping("/{id}/love")
    public ResponseEntity<Void> likeUser(@PathVariable("id") Long likeUserId) {
        this.recommendService.likeUser(likeUserId);
        return ResponseEntity.ok(null);
    }

    /**
     * 不喜欢
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity<Void> dislikeUser(@PathVariable("id") Long likeUserId) {
        this.recommendService.dislikeUser(likeUserId);
        return ResponseEntity.ok(null);
    }


    /**
     * 搜附近
     */
    @GetMapping("/search")
    public ResponseEntity queryNearby(String gender,
                                      @RequestParam(defaultValue = "2000") String distance) {
        List<NearUserVo> list = this.recommendService.queryNearby(gender, distance);
        System.err.println(list);
        return ResponseEntity.ok(list);
    }
}

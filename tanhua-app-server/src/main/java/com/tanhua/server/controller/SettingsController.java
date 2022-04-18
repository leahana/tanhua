package com.tanhua.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.tanhua.model.domain.Question;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 10:45
 * @Desc: 个人设置
 */

@RestController
@RequestMapping("/users")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    /**
     * 获取用户通用设置
     *
     * @return 返回用户通用设置
     */
    @GetMapping("/settings")
    public ResponseEntity<SettingsVo> querySettings() {

        SettingsVo vo = settingsService.querySettingsById();

        return ResponseEntity.ok(vo);
    }

    /**
     * 更新用户陌生人问题设置
     * @param map 问题
     * @return 返回更新结果
     */
    @PostMapping("/questions")
    public ResponseEntity updateQuestion(@RequestBody Map map) {

        String content = (String) map.get("content");

        settingsService.updateQuestion(content);

        return ResponseEntity.ok(null);
    }

    /**
     * 通知设置
     * @param map 通知设置
     * @return 返回更新结果
     */
    @PostMapping("/notifications/setting")
    public  ResponseEntity updateNotification(@RequestBody Map map) {

        settingsService.updateNotificationSetting(map);
        return ResponseEntity.ok(null);
    }


    /**
     * 分页查询黑名单列表
     * @param page,size 分页参数
     */

    @GetMapping("/blacklist")
    public ResponseEntity queryBlackList(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        // 1.调用service 进行查询
        PageResult pageResult=settingsService.queryBlackList(page,size);
        // 2.构造放回
        return ResponseEntity.ok(pageResult);
    }


    /**
     * 取消黑名单
     */
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity deleteBlackList(@PathVariable("uid") Long uid) {

        settingsService.removeBlackList(uid);

        return ResponseEntity.ok(null);
    }
}

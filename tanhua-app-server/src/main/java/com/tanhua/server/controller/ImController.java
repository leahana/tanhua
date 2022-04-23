package com.tanhua.server.controller;

import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.ImService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 12:52
 * @Desc: 环信
 */

@RestController
@RequestMapping("/huanxin")
public class ImController {

    @Autowired
    private ImService imService;


    /**
     * 查询环信的账号密码
     */
    @GetMapping("/user")
    public ResponseEntity getUser() {
        HuanXinUserVo vo = imService.getUser();
        return ResponseEntity.ok(vo);
    }

}
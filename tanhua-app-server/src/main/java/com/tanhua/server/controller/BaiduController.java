package com.tanhua.server.controller;

import com.tanhua.server.service.BaiduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 11:52
 * @Desc: 地址
 */
@RestController
@RequestMapping("/baidu")
public class BaiduController {

    @Autowired
    private BaiduService baiduService;

    /**
     * 更新位置
     */
    @PostMapping("/location")
    public ResponseEntity updateLocation(@RequestBody Map param) {
        Double longitude = Double.valueOf(param.get("longitude").toString());
        Double latitude = Double.valueOf(param.get("latitude").toString());
        String address = param.get("addrStr").toString();
        this.baiduService.updateLocation(longitude, latitude,address);
        return ResponseEntity.ok(null);
    }
}
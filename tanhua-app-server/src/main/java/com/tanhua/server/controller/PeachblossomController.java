package com.tanhua.server.controller;

import com.tanhua.model.mongo.Sound;
import com.tanhua.model.vo.SoundVo;
import com.tanhua.server.service.PeachblossomService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: leah_ana
 * @Date: 2022/4/19 19:19
 * @Desc: 桃花传音
 */

@RestController
@RequestMapping("/peachblossom")
public class PeachblossomController {
    @Autowired
    private PeachblossomService peachblossomService;

    @PostMapping
    public ResponseEntity addSound(MultipartFile soundFile) throws IOException {
        peachblossomService.addSound(soundFile);
        return ResponseEntity.ok(null);
    }

    @GetMapping
    public ResponseEntity getSound() {
        SoundVo vo = peachblossomService.getSound();
        return ResponseEntity.ok(vo);
    }
}

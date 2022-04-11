package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.MovementService;
import org.apache.commons.lang.enums.Enum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: leah_ana
 * @Date: 2022/4/11 15:02
 * @Desc: 动态
 */

@RestController
@RequestMapping("/movements")
public class MovementController {

    @Autowired
    private MovementService movementService;

    /**
     * 发布动态
     */
    @PostMapping
    public ResponseEntity addMovement(Movement movement,
                                      MultipartFile[] imageContent) throws IOException {

        movementService.publishMovement(movement, imageContent);

        return ResponseEntity.ok(null);
    }

    /**
     * 查看我的动态
     */
    @GetMapping("/all")
    public ResponseEntity queryMovementsByUserId(Long userId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "10") Integer pagesize) {

        PageResult pageResult = movementService.queryMovementsByUserId(userId, page, pagesize);

        return ResponseEntity.ok(pageResult);
    }
}

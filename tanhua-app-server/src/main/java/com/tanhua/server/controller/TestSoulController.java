package com.tanhua.server.controller;

import com.tanhua.model.mongo.TestPaper;
import com.tanhua.model.vo.ReportVo;
import com.tanhua.model.vo.TestPaperVo;
import com.tanhua.server.service.TestSoulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 14:27
 */

@RestController
@RequestMapping("/testSoul")
public class TestSoulController {
    @Autowired
    private TestSoulService testSoulService;

    /**
     * 获取试卷列表
     *
     * @return
     */
    @GetMapping
    public ResponseEntity getInformation() {

        System.err.println("获取试卷列表!!!!!!!!!!!!!!!!!!");

        //[
        // TestPaperVo(id=62611242d6513f556e5eeaa8,
        // name=初级灵魂题,
        // cover=https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_01.png,
        // level=初级,
        // star=2,
        // questions=[
        // Question(id=62611242d6513f556e5eeaa9,question=问题1, options=[Option(id=1, option=选项1),Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]),
        // Question(id=62611242d6513f556e5eeaaa, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeaab, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeaac, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeaad, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeaae, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeaaf, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeab0, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeab1, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=62611242d6513f556e5eeab2, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)])],
        // updated=1650528834473,
        // created=1650528834473,
        // reportId=7,
        // isLock=0),
        // TestPaperVo(id=626112f18949243e25f7f76d, name=中级灵魂题, cover=https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_02.png, level=中级, star=4, questions=[Question(id=626112f18949243e25f7f76e, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f76f, question=问题2, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f770, question=问题3, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f771, question=问题4, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f772, question=问题5, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f773, question=问题6, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f774, question=问题7, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f775, question=问题8, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f776, question=问题9, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626112f18949243e25f7f777, question=问题10, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)])], updated=1650529009525, created=1650529009525, reportId=8, isLock=0),
        // TestPaperVo(id=626113163c46e974e6f90df5, name=高级灵魂题, cover=https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/qn_cover_03.png, level=高级, star=5, questions=[Question(id=626113163c46e974e6f90df6, question=问题1, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90df7, question=问题2, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90df8, question=问题3, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90df9, question=问题4, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90dfa, question=问题5, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90dfb, question=问题6, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90dfc, question=问题7, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90dfd, question=问题8, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90dfe, question=问题9, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)]), Question(id=626113163c46e974e6f90dff, question=问题10, options=[Option(id=1, option=选项1), Option(id=2, option=选项2), Option(id=3, option=选项3), Option(id=4, option=选项4)])], updated=1650529046550, created=1650529046550, reportId=9, isLock=0)]
        List<TestPaperVo> vos = testSoulService.getInformation();
        System.err.println(vos);
        return ResponseEntity.ok(vos);
    }

    @PostMapping
    public ResponseEntity saveAnswers(@RequestBody Map map) {

        System.err.println("提交试卷!!!!!!!!!!!!!!!!!!");
        map.forEach((k, v) -> System.err.println(k + ":" + v));
        testSoulService.saveAnswers(map);
        return ResponseEntity.ok("");
    }
    @GetMapping("/report/{id}")
    public  ResponseEntity getReport(@PathVariable("id") String reportId){
        System.err.println("获取报告!!!!!!!!!!!!!!!!!!"+":"+reportId);
        ReportVo report = testSoulService.getReport(reportId);
        System.err.println("获取报告!!!!!!!!!!!!!!!!!!"+":"+report);
        return ResponseEntity.ok(report);
    }
}
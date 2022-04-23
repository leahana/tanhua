package com.tanhua.api;

import com.tanhua.model.mongo.TestPaper;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 15:11
 */

public interface TestPaperApi {

    //获取测试试卷
    List<TestPaper> getTestPaper();

    //获取测试试卷id
    Long getTestPaperId(Long valueOf);
}

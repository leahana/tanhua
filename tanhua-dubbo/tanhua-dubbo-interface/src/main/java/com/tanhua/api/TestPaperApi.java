package com.tanhua.api;

import com.tanhua.model.mongo.TestPaper;

import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 15:11
 */

public interface TestPaperApi {

    List<TestPaper> getTestPaper();

    Long getTestPaperId(Long valueOf);
}

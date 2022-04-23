package com.tanhua.api;

import com.tanhua.model.mongo.IdentificationResult;

public interface IdentificationResultApi {

    //根据结果获取灵魂测试结果
    IdentificationResult getResultByType(Integer type);
}

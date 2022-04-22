package com.tanhua.api;

import com.tanhua.model.mongo.IdentificationResult;

public interface IdentificationResultApi {
    IdentificationResult getResultByType(Integer type);
}

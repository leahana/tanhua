package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import lombok.Data;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 23:10
 * @Desc: 自定义异常
 */

@Data
public class BusinessException extends RuntimeException {

    private ErrorResult errorResult;

    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}

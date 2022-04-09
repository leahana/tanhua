package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @Author: leah_ana
 * @Date: 2022/4/9 23:25
 * @Desc: 自定义异常处理类
 * 1. 通过注解, 将异常处理类添加到spring容器中
 * 2. 编写方法,在方法内部处理异常,响应数据
 * 3. 方法上面编写注解,指定此方法可以出的异常类型
 */
@ControllerAdvice
public class ExceptionAdvice {

    //处理BusinessException业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handlerException(BusinessException be) {
        be.printStackTrace();
        ErrorResult errorResult = be.getErrorResult();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    //处理Exception系统异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity  handlerSystemException(Exception e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
    }
}

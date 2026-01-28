package com.archer.admin.web.exception;

import com.archer.admin.web.component.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 处理特定类型的异常，比如自定义异常CustomException
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result> handleCustomException(BizException e) {
        return new ResponseEntity(Result.error(e.getErrorCode(), e.getErrorMessage()), HttpStatus.OK);
    }

}

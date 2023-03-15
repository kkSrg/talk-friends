package com.tanhua.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
* 统一异常处理器
* */
@RestControllerAdvice
public class MyExceptionHandler {

    /*
    * 对ConsumerException的异常抓取并处理
    * */
    @ExceptionHandler(ConsumerException.class)
    public ResponseEntity consumerException(ConsumerException ce){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ce.getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("未知异常");
    }
}

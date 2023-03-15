package com.tanhua.exception;
/*
* 自定义一个异常类
* */
public class ConsumerException extends RuntimeException{
    public ConsumerException(String msg){//自定义异常输出信息
        super(msg);
    }
}

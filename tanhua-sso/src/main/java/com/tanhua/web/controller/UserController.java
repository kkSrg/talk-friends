package com.tanhua.web.controller;

import com.tanhua.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
* controller不处理业务逻辑关系
*   ResponseEntity
* */
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 1、发送短信验证码
     * @param params
     */
    @PostMapping("login")
    public ResponseEntity login(@RequestBody Map<String,String> params){
        //获取手机号
        String phone = params.get("phone");
        //调用业务层
        userService.sendCode(phone);
        return ResponseEntity.ok(null);
    }


    /**
     * 2、手机号和验证校验
     * @param params
     * @return
     */
    @PostMapping("loginVerification")
    public ResponseEntity<Map<String,Object>> loginVerification(@RequestBody Map<String,String> params){
        //调用业务层
        Map<String,Object> result=userService.loginVerification(params);
        return ResponseEntity.ok(result);
    }

    /**
     * 3、向外提供一个token校验接口
     */
    @GetMapping("/{token}")
    public Long parseToken(@PathVariable("token") String token){
       return userService.getToken(token);
    }

}

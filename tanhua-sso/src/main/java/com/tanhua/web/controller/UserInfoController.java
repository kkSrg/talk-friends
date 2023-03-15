package com.tanhua.web.controller;

import com.tanhua.db.dto.UserInfoDto;
import com.tanhua.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 3、完善个人信息
     * @param dto
     * @return
     */
    @PostMapping("loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfoDto dto){
        userInfoService.loginReginfo(dto);
        return ResponseEntity.ok(null);
    }

    /**
     * 4、补充头像
     * @return
     */
    @PostMapping("loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto){
        userInfoService.head(headPhoto);
        return ResponseEntity.ok(null);
    }
}

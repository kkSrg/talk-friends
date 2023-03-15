package com.tanhua.mongo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ContactVo implements Serializable {

    private Long id;
    private String userId;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;


}


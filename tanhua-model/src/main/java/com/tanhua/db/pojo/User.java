package com.tanhua.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User extends BasePojo implements Serializable {
    private Long id;
    private String mobile;
    private String password;


    //环信相关两个属性
    private String hxUser;
    private String hxPassword;
}

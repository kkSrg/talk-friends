package com.tanhua.db.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tanhua.db.pojo.BasePojo;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVo extends BasePojo implements Serializable {

    private Long id;
    private String avatar;
    private String tags;
    private String age;
    private String education;
    private String coverPic;
    private String profession;
    private String income;
    private String gender;
    private String nickname;
    private String birthday;
    private String city;
    private int marriage;
}

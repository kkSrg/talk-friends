package com.tanhua.db.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo extends BasePojo implements Serializable {

    @TableId(type = IdType.INPUT)
    private Long id;
    private String avatar;
    private String tags;
    private Integer age;
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

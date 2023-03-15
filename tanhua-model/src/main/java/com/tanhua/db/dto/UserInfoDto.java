package com.tanhua.db.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDto implements Serializable {
    private String gender;
    private String nickname;
    private String birthday;
    private String city;
}

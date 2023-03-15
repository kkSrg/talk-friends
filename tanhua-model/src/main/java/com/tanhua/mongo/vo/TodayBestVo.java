package com.tanhua.mongo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TodayBestVo implements Serializable {

    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
    private Integer fateValue;
}

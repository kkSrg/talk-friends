package com.tanhua.db.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class SoundVo implements Serializable {
    private Long id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String soundUrl;
    private Integer remainingTimes;
}

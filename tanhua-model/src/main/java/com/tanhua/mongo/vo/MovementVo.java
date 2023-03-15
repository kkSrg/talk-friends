package com.tanhua.mongo.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MovementVo implements Serializable {

    private String id;
    private Long userId;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
    private String textContent;
   // private String[] imageContent;
    private List<String> imageContent;
    private String distance;
    private String createDate;
    private Integer likeCount=0;
    private Integer commentCount=0;
    private Integer loveCount=0;
    private Integer hasLiked=0;
    private Integer hasloved=0;
}

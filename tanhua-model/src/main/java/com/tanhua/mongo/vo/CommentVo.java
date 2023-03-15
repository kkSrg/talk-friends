package com.tanhua.mongo.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentVo implements Serializable {
    private String id;
    private String avatar;
    private String nickname;
    private String content;
    private String createDate;
    private Integer likeCount;
    private Integer hasLiked;
}

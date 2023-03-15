package com.tanhua.db.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessagesVo implements Serializable {
    private String id;
    private String avatar;
    private String nickname;
    private String createDate;
}

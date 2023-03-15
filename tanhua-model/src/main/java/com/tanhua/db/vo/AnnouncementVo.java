package com.tanhua.db.vo;

import com.tanhua.db.pojo.BasePojo;
import lombok.Data;

import java.io.Serializable;

@Data
public class AnnouncementVo extends BasePojo implements Serializable {

    private String id;
    private String title; //标题
    private String description; //描述
    private String createDate;
}

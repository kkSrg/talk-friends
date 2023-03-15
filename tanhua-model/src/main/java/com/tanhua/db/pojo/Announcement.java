package com.tanhua.db.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Announcement implements Serializable {

    private Long id;
    private String title; //标题
    private String description; //描述
    private Date created;
    private Date updated;
}

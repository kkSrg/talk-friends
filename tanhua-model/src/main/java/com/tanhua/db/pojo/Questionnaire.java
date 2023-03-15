package com.tanhua.db.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Questionnaire implements Serializable {

    private Long id;
    private String name;
    private String cover;
    private String level;
    private Integer star;
    private Date updated;
    private Date created;


}

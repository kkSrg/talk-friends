package com.tanhua.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Options implements Serializable {

    private Long id;
    private Long sqId;
    private String options;
    private Integer score;
}

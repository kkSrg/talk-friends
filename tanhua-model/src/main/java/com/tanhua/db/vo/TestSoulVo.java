package com.tanhua.db.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TestSoulVo implements Serializable {

    private String id;
    private String name;
    private String cover;
    private String level;
    private Integer star;
    private List<SoulQuestionVo> questions;
    private Integer isLock;
    private String reportId;
}

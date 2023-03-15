package com.tanhua.db.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SoulQuestionVo implements Serializable {

    private String id;
    private String question;
    private List<OptionsVo> options;
}

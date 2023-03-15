package com.tanhua.db.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnswersDto implements Serializable {
    private String questionId;//问题ID
    private String optionId;//选项ID

}

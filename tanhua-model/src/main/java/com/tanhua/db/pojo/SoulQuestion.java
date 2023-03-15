package com.tanhua.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SoulQuestion implements Serializable {

    private Long id;
    private Long qId;
    private String question;
}

package com.tanhua.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Conclusion implements Serializable {

    private Long id;
    private String conclusion;
    private String cover;

}

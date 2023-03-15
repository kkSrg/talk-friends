package com.tanhua.mongo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RecommendationDto implements Serializable {
    private String gender;
    private String lastLogin;
    private Integer age;
    private String city;
    private String education;
}

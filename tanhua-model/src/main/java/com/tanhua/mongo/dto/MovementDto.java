package com.tanhua.mongo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MovementDto implements Serializable {
    private String textContent;
    private String longitude;
    private String latitude;
    private String location;
}

package com.tanhua.mongo.pojo;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

@Data
public class Movement implements Serializable {

    private ObjectId id;
    private Long pid;
    private Long userId;
    private String textContent;
    //private String[] medias;
    private List<String> medias;
    private Integer state;
    private String longitude;
    private String latitude;
    private String locationName;
    private Long created;

    private Integer commentCount=0;
    private Integer likeCount=0;
    private Integer loveCount=0;
}

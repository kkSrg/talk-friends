package com.tanhua.mongo.pojo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document("recommend_user")
public class RecommendUser implements Serializable {

    private ObjectId id;
    private Long userId;
    private Long toUserId;
    private Double score;
    private String date;
}

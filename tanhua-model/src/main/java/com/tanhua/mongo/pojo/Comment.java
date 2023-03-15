package com.tanhua.mongo.pojo;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document("comment")
public class Comment implements Serializable {
    private ObjectId id;
    private ObjectId publishId;
    private Integer commentType;
    private String content;
    private Long userId;
    private Long publishUserId;
    private Long created;
}

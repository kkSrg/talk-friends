package com.tanhua.mongo.pojo;

import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;

@Data
public class Friend implements Serializable {

    private ObjectId id;
    private Long userId;
    private Long friendId;
    private Long created;
}

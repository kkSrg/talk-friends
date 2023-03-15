package com.tanhua.mongo.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

//用户关注列表
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "focus_user")
public class FocusUser implements Serializable {
    private ObjectId id;  //主键
    private Long userId;  //用户id
    private Long followUserId;  //关注用户的id
    private Long created; //关注时间
}

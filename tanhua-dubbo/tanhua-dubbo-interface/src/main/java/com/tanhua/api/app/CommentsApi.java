package com.tanhua.api.app;

import com.tanhua.mongo.pojo.Comment;

import java.util.List;

public interface CommentsApi {
    List<Comment> findAllByPublishId(String movementId, Integer page, Integer pagesize);
}

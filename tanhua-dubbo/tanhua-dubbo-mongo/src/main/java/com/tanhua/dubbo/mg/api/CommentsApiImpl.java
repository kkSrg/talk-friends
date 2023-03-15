package com.tanhua.dubbo.mg.api;

import cn.hutool.core.convert.Convert;
import com.tanhua.api.app.CommentsApi;
import com.tanhua.enums.CommentType;
import com.tanhua.mongo.pojo.Comment;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class CommentsApiImpl implements CommentsApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Comment> findAllByPublishId(String movementId, Integer page, Integer pagesize) {
        Pageable pageable= PageRequest.of(page-1,pagesize, Sort.by(Sort.Order.desc("created")));
        //动态id  评论类型comment
        Query query=Query.query(Criteria.where("publishId").is(new ObjectId(movementId))
                                        //.and("commentType").is(Convert.toStr(CommentType.COMMENT.getType())))
                                        .and("commentType").is(CommentType.COMMENT.getType()))
                                        .with(pageable);
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        return commentList;
    }
}

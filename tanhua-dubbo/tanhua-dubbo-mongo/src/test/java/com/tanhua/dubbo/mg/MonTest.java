package com.tanhua.dubbo.mg;

import cn.hutool.core.convert.Convert;
import com.tanhua.enums.CommentType;
import com.tanhua.mongo.pojo.Comment;
import com.tanhua.mongo.pojo.RecommendUser;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class MonTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void test1(){
        List<RecommendUser> toUserId = mongoTemplate.find(Query.query(Criteria.where("toUserId").is(1L)), RecommendUser.class);
        System.out.println(toUserId);
    }
    @Test
    public void test2(){
        Pageable pageable= PageRequest.of(0,10, Sort.by(Sort.Order.desc("created")));
        //动态id  评论类型comment
        Query query=Query.query(Criteria.where("publishId").is(new ObjectId("5f0d78a35a319e6efab7fb60"))
                        //.and("commentType").is(Convert.toStr(CommentType.COMMENT.getType())));
                        .and("commentType").is(CommentType.COMMENT.getType()));
                //.with(pageable);
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);
        System.out.println(commentList);
    }

}

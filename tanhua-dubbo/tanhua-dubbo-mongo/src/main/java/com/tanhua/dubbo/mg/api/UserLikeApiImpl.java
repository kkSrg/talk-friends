package com.tanhua.dubbo.mg.api;

import com.tanhua.api.app.UserLikeApi;
import com.tanhua.mongo.pojo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class UserLikeApiImpl implements UserLikeApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike) {
        try {
            //1、查询数据
            Query query = Query.query(Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            //2、如果不存在，保存
            if(userLike == null) {
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setIsLike(isLike);
                mongoTemplate.save(userLike);
            }else {
                //3、更新
                Update update = Update.update("isLike", isLike)
                        .set("updated",System.currentTimeMillis());
                mongoTemplate.updateFirst(query,update,UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Long> findById(Long id) {
        Query query = Query.query(Criteria.where("likeUserId").is(id).and("isLike").is(true));
        List<UserLike> userLikes = mongoTemplate.find(query, UserLike.class);
        List<Long> ids=new ArrayList<>();
        userLikes.stream().map(userLike -> {
            ids.add(userLike.getUserId());
            return ids;
        }).collect(Collectors.toList());
        return ids;
    }
}

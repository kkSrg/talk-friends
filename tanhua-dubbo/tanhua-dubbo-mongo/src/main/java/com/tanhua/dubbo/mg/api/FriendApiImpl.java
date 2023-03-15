package com.tanhua.dubbo.mg.api;

import com.tanhua.api.app.FriendApi;
import com.tanhua.mongo.pojo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class FriendApiImpl implements FriendApi {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Friend> findByFriendId(Long fid) {
        Query query=Query.query(Criteria.where("userId").is(fid));
        return mongoTemplate.find(query,Friend.class);
    }

    @Override
    public void addContact(Long id, Long friendId) {
        // 双向添加才是好友
        Friend friend = new Friend();
        friend.setUserId(id);
        friend.setFriendId(friendId);
        friend.setCreated(System.currentTimeMillis());
        mongoTemplate.save(friend);

        Friend toFriend = new Friend();
        toFriend.setUserId(friendId);
        toFriend.setFriendId(id);
        toFriend.setCreated(System.currentTimeMillis());
        mongoTemplate.save(toFriend);
    }

    @Override
    public void deleteContact(Long id, Long friendId) {
        Query query = Query.query(Criteria.where("userId").is(id).and("friendId").is(friendId));
        mongoTemplate.remove(query, Friend.class);

        Query query1 = Query.query(Criteria.where("userId").is(friendId).and("friendId").is(id));
        mongoTemplate.remove(query1, Friend.class);
    }
}

package com.tanhua.api.app;

import com.tanhua.mongo.pojo.Friend;

import java.util.List;

public interface FriendApi {

    //根据friendId查询其所有好友的userId
    List<Friend> findByFriendId(Long fid);

    void addContact(Long id, Long friendId);

    void deleteContact(Long id, Long friendId);
}

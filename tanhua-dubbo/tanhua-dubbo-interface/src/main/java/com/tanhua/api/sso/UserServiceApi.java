package com.tanhua.api.sso;

import com.tanhua.db.pojo.User;

import java.util.List;

public interface UserServiceApi {
    //根据手机号查询用户信息
    User findByPhone(String phone);
    //保存新用户，返回保存成功的id
    Long save(User user);

    User findById(Long userId);

    List<User> findAll();

    void update(User user);

    User findByHxUser(String huanxinId);
}

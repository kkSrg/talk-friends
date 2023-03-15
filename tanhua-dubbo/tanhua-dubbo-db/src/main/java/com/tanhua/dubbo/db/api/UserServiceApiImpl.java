package com.tanhua.dubbo.db.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.api.sso.UserServiceApi;
import com.tanhua.dubbo.db.mapper.UserMapper;
import com.tanhua.db.pojo.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class UserServiceApiImpl implements UserServiceApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(User::getMobile,phone);
        //User user = userMapper.selectOne(wrapper);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public User findById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public User findByHxUser(String huanxinId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getHxUser,huanxinId);
        return userMapper.selectOne(wrapper);
    }
}

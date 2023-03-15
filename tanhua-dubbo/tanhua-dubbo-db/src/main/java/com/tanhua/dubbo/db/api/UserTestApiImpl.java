package com.tanhua.dubbo.db.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.api.app.UserTestApi;
import com.tanhua.db.pojo.UserTest;
import com.tanhua.dubbo.db.mapper.UserTestMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class UserTestApiImpl implements UserTestApi {

    @Autowired
    private UserTestMapper userTestMapper;

    @Override
    public Long save(UserTest userTest) {
        userTestMapper.insert(userTest);
        return userTest.getId();
    }

    @Override
    public UserTest findById(Long id) {
        return userTestMapper.selectById(id);
    }

    @Override
    public List<Long> getByConclusionId(Integer conclusionId, Long uid) {
        LambdaQueryWrapper<UserTest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTest::getConclusionId, conclusionId).ne(UserTest::getUserId, uid);
        List<UserTest> userTestList = userTestMapper.selectList(wrapper);
        return CollUtil.getFieldValues(userTestList, "userId", Long.class);
    }

    @Override
    public Long findByUserId(Long userId) {
        LambdaQueryWrapper<UserTest> lqw=new LambdaQueryWrapper<>();
        lqw.eq(UserTest::getUserId,userId);
        return userTestMapper.selectOne(lqw).getId();
    }


}

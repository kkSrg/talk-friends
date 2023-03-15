package com.tanhua.dubbo.db.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.dubbo.db.mapper.UserInfoMapper;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.mongo.dto.RecommendationDto;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class UserInfoServiceApiImpl implements UserInfoServiceApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void updateAvatar(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long userId) {
        return userInfoMapper.selectById(userId);
    }

    @Override
    public UserInfo findByIdAndCondation(Long userId, RecommendationDto dto) {

        LambdaQueryWrapper<UserInfo> query=new LambdaQueryWrapper<>();
        query.eq(UserInfo::getId,userId);
        //判断dto中的条件
        //gender
        query.eq(StrUtil.isNotBlank(dto.getGender()),UserInfo::getGender,dto.getGender());
        //age
        //query.eq(dto.getAge()!=null,UserInfo::getAge,dto.getAge());
        query.le(dto.getAge()!=null,UserInfo::getAge,dto.getAge());
        UserInfo userInfo = userInfoMapper.selectOne(query);
        return userInfo;
    }

    @Override
    public List<UserInfo> findByIds(List<Object> userIds) {
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId,userIds);
        return userInfoMapper.selectList(wrapper);
    }

    @Override
    public List<UserInfo> findByIdsPageAndKw(List<Long> fid, String keyword, Integer page, Integer pagesize) {
        //创建分页对象，设置分页参数
        //注意：使用分页，需要配置分页插件
        IPage<UserInfo> pg=new Page<UserInfo>(page,pagesize);

        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId,fid);
        //如果keyword存在则有条件查询，模糊查询
        wrapper.like(StrUtil.isNotBlank(keyword),UserInfo::getNickname,keyword);
        pg=userInfoMapper.selectPage(pg,wrapper);
        return pg.getRecords();
    }

    @Override
    public List<UserInfo> findUserInfoByAndGender(List<Long> ids, String gender) {
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getId,ids);
        wrapper.eq(StrUtil.isNotBlank(gender),UserInfo::getGender,gender);
        return userInfoMapper.selectList(wrapper);
    }

    @Override
    public UserInfo slectById(Long userId) {
        return userInfoMapper.selectById(userId);
    }
}

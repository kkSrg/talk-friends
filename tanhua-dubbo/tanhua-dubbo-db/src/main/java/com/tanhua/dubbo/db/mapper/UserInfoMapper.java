package com.tanhua.dubbo.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.db.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}

package com.tanhua.app.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.tanhua.api.app.UserLocationApi;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.mongo.vo.NearUserVo;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLocationService {

    @DubboReference
    private UserLocationApi userLocationApi;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    /**
     * 7. 搜附近
     * @param gender
     * @param distance
     */
    public List<NearUserVo> search(String gender, String distance) {
        Long uid = ThreadLocalUtil.getId();
        //1. 根据登录者uid,查询范围内所有用户的id
        List<Long> ids = userLocationApi.search(uid, Convert.toDouble(distance));
        //2. 查询ids的所有用户信息,带上性别作为条件
        List<UserInfo> userInfoList = userInfoServiceApi.findUserInfoByAndGender(ids,gender);
        //3. 遍历userInfoList,封装List<NearUserVo>数据
        List<NearUserVo> list = userInfoList.stream().map(userInfo -> {
            NearUserVo vo = new NearUserVo();
            vo.setUserId(userInfo.getId());
            vo.setNickname(userInfo.getNickname());
            vo.setAvatar(userInfo.getAvatar());
            return vo;
        }).collect(Collectors.toList());
        return list;
    }
}

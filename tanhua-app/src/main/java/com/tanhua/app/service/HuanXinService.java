package com.tanhua.app.service;

import com.tanhua.api.sso.UserServiceApi;
import com.tanhua.db.pojo.User;
import com.tanhua.db.vo.HuanXinUserVo;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HuanXinService {

    @DubboReference
    private UserServiceApi userServiceApi;

    /**
     * 查询当前用户的环信账号
     *  1、获取用户id，根据账号规则拼接
     *  2、获取用户id，查询用户对象
     */
    public HuanXinUserVo findHuanXinUser() {
        Long userId = ThreadLocalUtil.getId();
        User user = userServiceApi.findById(userId);
        if(user == null) {
            return null;
        }
        return new HuanXinUserVo(user.getHxUser(),user.getHxPassword());
    }
}

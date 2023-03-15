package com.tanhua.app;


import com.tanhua.api.sso.UserServiceApi;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.db.pojo.User;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuanXinTest {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserServiceApi userServiceApi;

    //批量注册
    @Test
    public void register() {
        List<User> users = userServiceApi.findAll();
        for (User user : users) {
            Boolean create = huanXinTemplate.createUser("hx" + user.getId(), "123456");
            if (create){
                user.setHxUser("hx" + user.getId());
                user.setHxPassword("123456");
                userServiceApi.update(user);
            }
        }
    }

}

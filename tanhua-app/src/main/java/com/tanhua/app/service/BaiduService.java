package com.tanhua.app.service;

import com.tanhua.api.app.UserLocationApi;
import com.tanhua.exception.ConsumerException;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {

    @DubboReference
    private UserLocationApi userLocationApi;

    //更新地理位置
    public void updateLocation(Double longitude, Double latitude, String address) {
        Long id = ThreadLocalUtil.getId();
        Boolean flag = userLocationApi.updateLocation(id,longitude,latitude,address);
        if(!flag) {
            throw  new ConsumerException("地理位置更新失败");
        }
    }
}

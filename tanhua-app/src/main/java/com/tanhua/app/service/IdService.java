package com.tanhua.app.service;

import com.tanhua.enums.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//生成自增长的id，原理：使用redis的自增长值
@Service
public class IdService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Long createId(IdType idType) {
        String idKey = "TANHUA_ID_" + idType.toString();
        return this.redisTemplate.opsForValue().increment(idKey);
    }


    @Async
    public void testAsync(){
        throw new RuntimeException("xxxxxxxxx");
    }

}
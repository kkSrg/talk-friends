package com.tanhua.app;

import com.tanhua.api.app.RecommendUserApi;
import com.tanhua.mongo.pojo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MongoTest {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @Test
    public void testFind(){
        RecommendUser recommendUser = recommendUserApi.findByToUserId(1L);
        System.out.println(recommendUser);
    }

    @Test
    public void testFindAll(){
        List<RecommendUser> allByToUserId = recommendUserApi.findAllByToUserId(1L, 1, 10);
        System.out.println(allByToUserId);

    }
}

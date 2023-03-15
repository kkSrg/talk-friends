package com.tanhua.dubbo.mg.api;

import com.tanhua.api.app.VisitorsApi;
import com.tanhua.mongo.pojo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DubboService
public class VisitorsApiImpl implements VisitorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存访客数据
     *  对于同一个用户，一天之内只能保存一次访客数据
     */
    @Override
    public void save(Visitors visitors) {
        //1、查询访客数据
        Query query = Query.query(Criteria.where("userId").is(visitors.getUserId())
                .and("visitorUserId").is(visitors.getVisitorUserId())
                .and("visitDate").is(visitors.getVisitDate()));
        //2、不存在，保存
        if(!mongoTemplate.exists(query,Visitors.class)) {
            mongoTemplate.save(visitors);
        }
    }
}

package com.tanhua.dubbo.mg.api;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.api.app.SmallVideoApi;
import com.tanhua.mongo.pojo.FocusUser;
import com.tanhua.mongo.pojo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class SmallVideoApiImpl implements SmallVideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Video> findByVids(List<Long> vids, Integer page, Integer pagesize) {

        //根据vid查询
        Pageable pageable = PageRequest.of(page-1,pagesize, Sort.by(Sort.Order.desc("created")));
        Query query = Query.query(Criteria.where("vid").in(vids)).with(pageable);
        return mongoTemplate.find(query,Video.class);
    }

    @Override
    public List<Video> findAll(Integer page, Integer pagesize) {
        Pageable pageable = PageRequest.of(page-1,pagesize, Sort.by(Sort.Order.desc("created")));
        Query query = new Query();
        query.with(pageable);
        return mongoTemplate.find(query,Video.class);
    }

    @Override
    public void saveFocus(String uid, Long id) {
        if (isFocus(Convert.toLong(uid),id)){
            FocusUser focusUser = new FocusUser();
            focusUser.setFollowUserId(Convert.toLong(uid));
            focusUser.setUserId(id);
            focusUser.setCreated(System.currentTimeMillis());
            mongoTemplate.save(focusUser);
        }
    }

    //是否关注
    @Override
    public Boolean isFocus(Long id, Long uid) {
        Query query = Query.query(Criteria.where("userId").is(id).and("followUserId").is(uid));
        FocusUser focusUser = mongoTemplate.findOne(query,FocusUser.class);
        return ObjectUtil.isNull(focusUser)?true:false;
    }

    @Override
    public Boolean removeFocus(Long uid, Long id) {
        Query query = Query.query(Criteria.where("userId").is(id).and("followUserId").is(uid));
        DeleteResult result = mongoTemplate.remove(query, FocusUser.class);
        return result.getDeletedCount()==1?true:false;
    }

    @Override
    public void saveVideo(Video video) {
        mongoTemplate.save(video);
    }
}

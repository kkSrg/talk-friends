package com.tanhua.dubbo.mg.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.tanhua.api.app.RecommendUserApi;
import com.tanhua.mongo.pojo.RecommendUser;
import com.tanhua.mongo.pojo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser findByToUserId(Long id) {
        Query query=Query.query(Criteria.where("toUserId").is(id))
                          .with(Sort.by(Sort.Order.desc("score")))
                            .limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public List<RecommendUser> findAllByToUserId(Long id, Integer page, Integer pagesize) {

        Pageable pageable= PageRequest.of(page-1,pagesize,Sort.by(Sort.Order.desc("score")));
        Query query=Query.query(Criteria.where("toUserId").is(id)).with(pageable);
        return mongoTemplate.find(query, RecommendUser.class);
    }

    @Override
    public Integer findScore(Long id, Long uid) {
        Query query = Query.query(Criteria.where("toUserId").is(uid).and("userId").is(id));
        Double score = mongoTemplate.findOne(query, RecommendUser.class).getScore();
        return Convert.toInt(score);
    }

    /**
     * 1、排除喜欢，不喜欢的用户
     * 2、随机展示
     * 3、指定数量
     */
    public List<RecommendUser> queryCardsList(Long userId, int counts) {
        //1、查询喜欢不喜欢的用户ID
        List<UserLike> likeList = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
        List<Long> likeUserIdS = CollUtil.getFieldValues(likeList, "likeUserId", Long.class);
        //2、构造查询推荐用户的条件
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(likeUserIdS);
        //3、使用统计函数，随机获取推荐的用户列表
        TypedAggregation<RecommendUser> newAggregation = TypedAggregation.newAggregation(RecommendUser.class,
                Aggregation.match(criteria),//指定查询条件
                Aggregation.sample(counts)
        );
        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(newAggregation, RecommendUser.class);
        //4、构造返回
        return results.getMappedResults();
    }
}

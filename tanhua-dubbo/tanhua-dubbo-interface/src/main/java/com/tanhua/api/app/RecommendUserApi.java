package com.tanhua.api.app;

import com.tanhua.mongo.pojo.RecommendUser;

import java.util.List;

public interface RecommendUserApi {
    RecommendUser findByToUserId(Long id);

    List<RecommendUser> findAllByToUserId(Long id, Integer page, Integer pagesize);

    Integer findScore(Long id, Long uid);

    List<RecommendUser> queryCardsList(Long id, int i);
}

package com.tanhua.api.app;

import com.tanhua.mongo.pojo.Video;

import java.util.List;

public interface SmallVideoApi {


    List<Video> findAll(Integer page, Integer pagesize);

    List<Video> findByVids(List<Long> vids, Integer page, Integer pagesize);

    void saveFocus(String uid, Long id);

    //查询是否关注
    Boolean isFocus(Long id,Long uid);

    Boolean removeFocus(Long uid, Long id);

    void saveVideo(Video video);
}

package com.tanhua.api.app;

import com.tanhua.db.pojo.Announcement;

import java.util.List;

public interface AnnouncementApi {
    List<Announcement> findAll(Integer page, Integer pagesize);
}

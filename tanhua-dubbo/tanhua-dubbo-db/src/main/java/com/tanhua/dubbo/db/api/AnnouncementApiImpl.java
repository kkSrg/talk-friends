package com.tanhua.dubbo.db.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.api.app.AnnouncementApi;
import com.tanhua.db.pojo.Announcement;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.dubbo.db.mapper.AnnouncementMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@DubboService
public class AnnouncementApiImpl implements AnnouncementApi {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> findAll(Integer page, Integer pagesize) {
        //创建分页对象，设置分页参数
        //注意：使用分页，需要配置分页插件
        IPage<Announcement> pg=new Page<>(page,pagesize);
        LambdaQueryWrapper<Announcement> wrapper =new LambdaQueryWrapper<>();
        announcementMapper.selectPage(pg, wrapper);
        return pg.getRecords();
    }
}

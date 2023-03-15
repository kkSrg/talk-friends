package com.tanhua.dubbo.db.api;

import com.tanhua.api.app.ConclusionApi;
import com.tanhua.db.pojo.Conclusion;
import com.tanhua.dubbo.db.mapper.ConclusionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class ConclusionApiImpl implements ConclusionApi {

    @Autowired
    private ConclusionMapper conclusionMapper;

    @Override
    public Conclusion find(Integer conclusionId) {
        return conclusionMapper.selectById(conclusionId);
    }
}

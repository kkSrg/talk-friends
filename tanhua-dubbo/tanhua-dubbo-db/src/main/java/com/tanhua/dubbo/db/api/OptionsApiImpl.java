package com.tanhua.dubbo.db.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.api.app.OptionsApi;
import com.tanhua.db.pojo.Options;
import com.tanhua.dubbo.db.mapper.OptionsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class OptionsApiImpl implements OptionsApi {

    @Autowired
    private OptionsMapper optionsMapper;


    @Override
    public List<Options> getBySqId(Long sqId) {
        LambdaQueryWrapper<Options> lqw = new LambdaQueryWrapper<>();
        lqw.eq(sqId != null, Options::getSqId, sqId);
        List<Options> options = optionsMapper.selectList(lqw);
        return options;
    }

    @Override
    public Integer findScore(String questionId, String optionId) {
        LambdaQueryWrapper<Options> wrapper  = new LambdaQueryWrapper<>();
        wrapper.eq(Options::getSqId,questionId).eq(Options::getId,optionId);
        return optionsMapper.selectOne(wrapper).getScore();
    }
}

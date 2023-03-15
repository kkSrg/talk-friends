package com.tanhua.dubbo.db.api;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.api.app.SoulQuestionApi;
import com.tanhua.db.dto.AnswersDto;
import com.tanhua.db.pojo.SoulQuestion;
import com.tanhua.dubbo.db.mapper.SoulQuestionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class SoulQuestionApiImpl implements SoulQuestionApi {

    @Autowired
    private SoulQuestionMapper soulQuestionMapper;


    @Override
    public List<SoulQuestion> getByQId(Long qId) {
        LambdaQueryWrapper<SoulQuestion> lqw=new LambdaQueryWrapper<>();
        lqw.eq(qId!=null,SoulQuestion::getQId, qId);
        return soulQuestionMapper.selectList(lqw);
    }

    @Override
    public Long getQId(AnswersDto answersDto) {
        LambdaQueryWrapper<SoulQuestion> lqw=new LambdaQueryWrapper<>();
        lqw.eq(SoulQuestion::getId,answersDto.getQuestionId());
        return soulQuestionMapper.selectOne(lqw).getQId();
    }
}

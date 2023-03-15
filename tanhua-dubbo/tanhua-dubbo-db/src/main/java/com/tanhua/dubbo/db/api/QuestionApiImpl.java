package com.tanhua.dubbo.db.api;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.api.app.QuestionApi;
import com.tanhua.db.pojo.Question;
import com.tanhua.dubbo.db.mapper.QuestionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionApiImpl implements QuestionApi {
    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public String findTxtByUserId(Long userId) {

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getUserId,userId);
        Question question = questionMapper.selectOne(wrapper);
        if (question == null){
            return null;
        }
        return question.getTxt();
    }
}

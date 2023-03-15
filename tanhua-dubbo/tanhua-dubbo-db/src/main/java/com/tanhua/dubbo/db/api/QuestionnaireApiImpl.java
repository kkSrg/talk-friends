package com.tanhua.dubbo.db.api;

import com.tanhua.api.app.QuestionnaireApi;
import com.tanhua.db.pojo.Questionnaire;
import com.tanhua.dubbo.db.mapper.QuestionnaireMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class QuestionnaireApiImpl implements QuestionnaireApi {

    @Autowired
    private QuestionnaireMapper questionnaireMapper;


    @Override
    public List<Questionnaire> getAll() {
        return questionnaireMapper.selectList(null);
    }
}

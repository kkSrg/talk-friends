package com.tanhua.api.app;

import com.tanhua.db.pojo.Questionnaire;

import java.util.List;

public interface QuestionnaireApi {
    //查出全部等级的测试题
    List<Questionnaire> getAll();
}

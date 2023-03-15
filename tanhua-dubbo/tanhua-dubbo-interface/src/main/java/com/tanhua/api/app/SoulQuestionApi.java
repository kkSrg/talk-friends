package com.tanhua.api.app;

import com.tanhua.db.dto.AnswersDto;
import com.tanhua.db.pojo.SoulQuestion;


import java.util.List;

public interface SoulQuestionApi {
    //根据问卷id查出所有题目
    List<SoulQuestion> getByQId(Long qId);

    //根据题目id查询试题编号
    Long getQId(AnswersDto answersDto);
}

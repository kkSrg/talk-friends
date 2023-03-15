package com.tanhua.api.app;

import com.tanhua.db.pojo.Options;

import java.util.List;

public interface OptionsApi {
    //根据问题id查选项
    List<Options> getBySqId(Long sqId);

    Integer findScore(String questionId, String optionId);
}

package com.tanhua.api.app;

import com.tanhua.db.pojo.UserTest;


import java.util.List;

public interface UserTestApi {
    Long save(UserTest userTest);


    UserTest findById(Long id);

    List<Long> getByConclusionId(Integer conclusionId,Long uid);

    Long findByUserId(Long userId);
}

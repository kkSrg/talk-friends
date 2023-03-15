package com.tanhua.api.app;

import com.tanhua.db.pojo.ReceptionSound;

public interface ReceptionSoundApi {
    Integer save(ReceptionSound receptionSound);

    ReceptionSound findById(Long id);

    void reSaveTimes(Integer times);


    void update(Integer times, Long id);
}

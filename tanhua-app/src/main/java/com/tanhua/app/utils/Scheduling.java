package com.tanhua.app.utils;

import com.tanhua.api.app.ReceptionSoundApi;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduling {

    @DubboReference
    private ReceptionSoundApi receptionSoundApi;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void timeResave(){
        //重置次数
        receptionSoundApi.reSaveTimes(10);

    }
}

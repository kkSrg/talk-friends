package com.tanhua.dubbo.db.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tanhua.api.app.ReceptionSoundApi;
import com.tanhua.db.pojo.ReceptionSound;
import com.tanhua.dubbo.db.mapper.ReceptionSoundMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class ReceptionSoundApiImpl implements ReceptionSoundApi {

    @Autowired
    private ReceptionSoundMapper receptionSoundMapper;

    @Override
    public Integer save(ReceptionSound receptionSound) {
        receptionSoundMapper.insert(receptionSound);
        LambdaQueryWrapper<ReceptionSound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReceptionSound::getUserId,receptionSound.getUserId());
        return receptionSoundMapper.selectOne(wrapper).getRemainingTimes();
    }

    @Override
    public ReceptionSound findById(Long id) {
        LambdaQueryWrapper<ReceptionSound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReceptionSound::getUserId,id);
        ReceptionSound receptionSound = receptionSoundMapper.selectOne(wrapper);
        return receptionSound;
    }

    //定时器任务
    @Override
    public void reSaveTimes(Integer times) {
        LambdaUpdateWrapper<ReceptionSound> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(ReceptionSound::getRemainingTimes,times);
        receptionSoundMapper.update(null,wrapper);
    }

    @Override
    public void update(Integer times, Long id) {
        LambdaUpdateWrapper<ReceptionSound> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ReceptionSound::getUserId,id).set(ReceptionSound::getRemainingTimes,times);
        receptionSoundMapper.update(null,wrapper);
    }
}

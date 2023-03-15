package com.tanhua.dubbo.db.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.api.app.SoundApi;
import com.tanhua.db.pojo.Sound;
import com.tanhua.dubbo.db.mapper.SoundMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class SoundApiImpl implements SoundApi {

    @Autowired
    private SoundMapper soundMapper;

    @Override
    public void save(Sound sound) {
        soundMapper.insert(sound);
    }

    @Override
    public List<Sound> list(Long id) {
        LambdaQueryWrapper<Sound> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Sound::getUserId,id);
        List<Sound> sounds = soundMapper.selectList(wrapper);
        System.out.println(sounds);
        return sounds;
    }

    @Override
    public void deleteById(Long id) {
        soundMapper.deleteById(id);
    }
}

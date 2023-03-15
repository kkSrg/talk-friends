package com.tanhua.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.tanhua.api.app.ReceptionSoundApi;
import com.tanhua.api.app.SoundApi;
import com.tanhua.api.sso.UserInfoServiceApi;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.db.pojo.ReceptionSound;
import com.tanhua.db.pojo.Sound;
import com.tanhua.db.pojo.UserInfo;
import com.tanhua.db.vo.SoundVo;
import com.tanhua.exception.ConsumerException;
import com.tanhua.utils.ThreadLocalUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class PeachBlossomService {

    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private SoundApi soundApi;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference
    private UserInfoServiceApi userInfoServiceApi;

    @DubboReference
    private ReceptionSoundApi receptionSoundApi;
    /**
     * 发送语音
     * @param soundFile
     */
    public void save(MultipartFile soundFile) {
        try {
            Long id = ThreadLocalUtil.getId();
            //获取存储路径
            String path= ossTemplate.upload(soundFile.getOriginalFilename(),soundFile.getInputStream());
            Sound sound = new Sound();
            sound.setUserId(id);
            sound.setSoundUrl(path);
            sound.setCreateTime(new Date());
            sound.setLogicDel(0);
            soundApi.save(sound);
        } catch (IOException e) {
            throw new ConsumerException("发送失败");
        }
    }

    /**
     * 接收语音
     * @return
     */
    public SoundVo receptionSound() {
        //获取当前登录者id
        Long id = ThreadLocalUtil.getId();
        //获取所有语音(不包含自己的语音)
        List<Sound> list =  soundApi.list(id);

        if (list.size()==0){
            throw new ConsumerException("语音已被接收完");
        }
        //随机获取语音
        int numRan = Convert.toInt(Math.random() * list.size());
        Sound sound = list.get(numRan);

        //记录接收语音信息,保存到tb_reception_sound

        //先查询是否已有记录
        ReceptionSound receptionSound = receptionSoundApi.findById(id);

        Integer times = 9;
        //如果不存在,保存
        if (receptionSound == null){
            receptionSound = new ReceptionSound();
            //tb_reception_sound的ID为自设置,为当前用户id
            receptionSound.setUserId(id);
            receptionSound.setRemainingTimes(times);
            times = receptionSoundApi.save(receptionSound);
        }else {
            //如果存在,修改次数
            times = receptionSound.getRemainingTimes() - 1;
            receptionSound.setRemainingTimes(times);
            receptionSoundApi.update(times,id);
        }


        //封装数据
        SoundVo soundVo = new SoundVo();
        soundVo.setSoundUrl(sound.getSoundUrl());
        //获取用户数据
        UserInfo userInfo = userInfoServiceApi.findById(sound.getUserId());
        //封装数据
        BeanUtil.copyProperties(userInfo,soundVo,"id");
        soundVo.setId(sound.getUserId());
        //根据发送语音表ID去接收语音表查询sound_id;
        soundVo.setRemainingTimes(times);
        //删除该语音(逻辑删除)
        soundApi.deleteById(sound.getId());
        return soundVo;
    }
}

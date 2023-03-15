package com.tanhua.app.controller;

import com.tanhua.app.service.PeachBlossomService;
import com.tanhua.db.vo.SoundVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("peachblossom")
public class PeachBlossomController {

    @Autowired
    private PeachBlossomService peachBlossomService;

    /**
     * 发送语音
     * @param soundFile
     * @return
     */
    @PostMapping
    public ResponseEntity sendSound(MultipartFile soundFile){
        peachBlossomService.save(soundFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 接收语音
     * @return
     */
    @GetMapping
    public ResponseEntity<SoundVo> receptionSound(){
        SoundVo vo = peachBlossomService.receptionSound();
        return ResponseEntity.ok(vo);
    }
}

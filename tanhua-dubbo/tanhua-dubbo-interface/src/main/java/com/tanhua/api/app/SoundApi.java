package com.tanhua.api.app;

import com.tanhua.db.pojo.Sound;

import java.util.List;

public interface SoundApi {
    void save(Sound sound);

    List<Sound> list(Long soundId);

    void deleteById(Long id);
}

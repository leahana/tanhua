package com.tanhua.api;

import com.tanhua.model.mongo.Sound;

public interface SoundApi {
    String addSound(String soundUrl, Long userId);

    Sound randomSound(Long UserId);
}

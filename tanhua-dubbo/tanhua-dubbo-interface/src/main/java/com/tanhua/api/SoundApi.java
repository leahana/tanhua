package com.tanhua.api;

import com.tanhua.model.mongo.Sound;

public interface SoundApi {

    //保存桃花传音
    String saveSound(String soundUrl, Long userId);

    //随机获取声音(数据太少,没有做条件筛选
    Sound randomSound(Long UserId);
}

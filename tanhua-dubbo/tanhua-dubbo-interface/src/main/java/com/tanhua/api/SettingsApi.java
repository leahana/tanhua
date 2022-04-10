package com.tanhua.api;


import com.tanhua.model.domain.Settings;

public interface SettingsApi {
    //根据用户id查询通用设置
    Settings querySettingsByUserId(Long userId);

    void addSettings(Settings settings);

    void updateSettings(Settings settings);
}
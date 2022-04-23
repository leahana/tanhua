package com.tanhua.api;


import com.tanhua.model.domain.Settings;

public interface SettingsApi {

    //根据用户id查询通用设置
    Settings getSettings(Long userId);

    //保存设置
    void saveSettings(Settings settings);

    //更新设置
    void updateSettings(Settings settings);
}
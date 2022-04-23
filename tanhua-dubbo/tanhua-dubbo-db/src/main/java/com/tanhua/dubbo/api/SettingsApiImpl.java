package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.api.SettingsApi;
import com.tanhua.dubbo.mappers.SettingsMapper;
import com.tanhua.model.domain.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: leah_ana
 * @Date: 2022/4/10 10:43
 * @Desc: 通用设置实现类
 */

@DubboService
public class SettingsApiImpl implements SettingsApi {

    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public Settings getSettings(Long userId) {

        QueryWrapper<Settings> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("user_id", userId);

        return settingsMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveSettings(Settings settings) {
        settingsMapper.insert(settings);
    }

    @Override
    public void updateSettings(Settings settings) {
        settingsMapper.updateById(settings);
    }
}

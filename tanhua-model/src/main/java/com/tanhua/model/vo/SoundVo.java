package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Sound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: leah_ana
 * @Date: 2022/4/19 19:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoundVo {
    private Integer id;
    private String avatar; //头像
    private String nickname; //昵称
    private String gender;//性别
    private Integer age;
    private String soundUrl; //音频URL
    private Integer remainingTimes; //剩余次数

    public static SoundVo init(UserInfo userInfo, Sound sound) {
        SoundVo soundVo = new SoundVo();
        if (userInfo != null && sound != null) {
            soundVo.setId(Integer.valueOf(sound.getUserId().toString()));
            soundVo.setAvatar(userInfo.getAvatar());
            soundVo.setNickname(userInfo.getNickname());
            soundVo.setGender(userInfo.getGender());
            soundVo.setAge(userInfo.getAge());
            soundVo.setSoundUrl(sound.getSoundUrl());
        }
        return soundVo;
    }
}

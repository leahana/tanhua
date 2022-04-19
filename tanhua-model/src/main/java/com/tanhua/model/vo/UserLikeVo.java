package com.tanhua.model.vo;

import cn.hutool.core.util.RandomUtil;
import com.tanhua.model.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: leah_ana
 * @Date: 2022/4/18 21:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeVo implements Serializable {
    private Integer id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String city;
    private String education;
    private Integer marriage;
    private Integer matchRate;
    private Boolean alreadyLove;

    public static UserLikeVo  init(UserInfo userInfo) {
        UserLikeVo vo = new UserLikeVo();
        vo.setId(Integer.valueOf(userInfo.getId().toString()));
        vo.setAvatar(userInfo.getAvatar());
        vo.setNickname(userInfo.getNickname());
        vo.setGender(userInfo.getGender());
        vo.setAge(userInfo.getAge());
        vo.setCity(userInfo.getCity());
        vo.setEducation(userInfo.getEducation());
        vo.setMarriage(userInfo.getMarriage());
        vo.setMatchRate(RandomUtil.randomInt(30,100));
        return vo;
    }
}

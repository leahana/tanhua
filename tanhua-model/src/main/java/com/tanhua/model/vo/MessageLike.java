package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageLike {

    private String id;
    private String avatar;
    private String nickname;
    private String createDate;

    public static MessageLike init(UserInfo userInfo) {
        MessageLike vo = new MessageLike();
        System.out.println(userInfo);
        if(userInfo != null) {
            vo.setId(userInfo.getId().toString());
            vo.setAvatar(userInfo.getAvatar());
            vo.setNickname(userInfo.getNickname());
        }
        return vo;
    }

}

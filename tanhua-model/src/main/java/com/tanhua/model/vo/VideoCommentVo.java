package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.VideoComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: leah_ana
 * @Date: 2022/4/15 15:44
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCommentVo  implements Serializable {
    private String id;
    private String avatar;
    private String nickname;
    private String content;
    private String createDate;
    private Integer likeCount;
    private Integer hasLiked;

    public static VideoCommentVo init(UserInfo userInfo, VideoComment comment) {
        VideoCommentVo vo = new VideoCommentVo();
        BeanUtils.copyProperties(userInfo, vo);
        BeanUtils.copyProperties(comment, vo);
        vo.setHasLiked(0);
        Date date = new Date(comment.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        vo.setId(comment.getId().toHexString());
        return vo;
    }
}

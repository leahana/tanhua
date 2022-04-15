package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Author: leah_ana
 * @Date: 2022/4/15 11:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "video_comment")
public class VideoComment implements Serializable {

    private ObjectId id;
    private ObjectId videoId;    //视频id
    private Long userId;           //评论人
    private Integer commentType;   //评论类型，1-点赞，2-评论，3-喜欢
    //private Long toUserId;    //视频发表者id
    private Long created;     //发表时间
    private Long updated;     //更新时间
    private Boolean isLike;         //点赞
    private Integer likeCount;      //点赞数
    private String content;         //评论内容

}

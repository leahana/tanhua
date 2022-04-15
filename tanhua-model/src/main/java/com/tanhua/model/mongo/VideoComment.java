package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: leah_ana
 * @Date: 2022/4/15 11:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "video_comment")
public class VideoComment {

    private ObjectId id;
    private ObjectId videoId;    //视频id
    private Long userId;           //评论人
    private Long toUserId;    //视频发表者id
    private Long created; 		   //发表时间
    private Boolean isLike;         //点赞
}

package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

/**
 * @Author: leah_ana
 * @Date: 2022/4/12 21:30
 * @Desc: 记录评论点赞
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Comment_Evaluate")
public class CommentOfEvaluate implements Serializable {
    private Long id;
    private ObjectId commentId;    //评论id
    private Long userId;       //看评论用户id
}

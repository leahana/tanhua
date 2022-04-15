package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Author: leah_ana
 * @Date: 2022/4/14 23:07
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "focus_user")
public class FocusUser implements Serializable {
    private ObjectId id; //主键id
    private Long userId;
    private Long followUserId;
    private Long updated;
    private Boolean isFocus;
}

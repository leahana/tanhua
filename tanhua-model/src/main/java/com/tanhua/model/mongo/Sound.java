package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: leah_ana
 * @Date: 2022/4/19 19:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sound")
public class Sound implements java.io.Serializable {
    private ObjectId id; //主键id
    private Long sid; //自动增长
    private Long created; //创建时间
    private Long userId;
    private String soundUrl; //音频文件，URL
    private Boolean usable; //是否删除

}

package com.tanhua.model.mongo;

import com.tanhua.model.domain.KeyValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/22 15:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "identification_result")
//identification result//鉴定结果
public class IdentificationResult  implements java.io.Serializable {
    private ObjectId id;
    private Integer type;//鉴定类型这里用分数范围也可以
    private String conclusion;
    private String cover;
    private List<KeyValue> dimensions;
}


package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 14:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_question")
public class Question implements Serializable {
    private String id;//问题id
    private Long testPaperId;//试卷id
    private String question;//问题
    private List<Option> options;//选项
}

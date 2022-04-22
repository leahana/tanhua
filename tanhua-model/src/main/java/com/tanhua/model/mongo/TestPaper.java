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
 * @Date: 2022/4/21 14:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_paper")
public class TestPaper implements Serializable {
    private Long id; //问卷编号
    private String name;//问卷名称
    private String cover;//封面
    private String level;//等级
    private Integer star; //星别(2-5)
    //private List<Question> questions; //问题
    private Long updated;
    private Long created;
}

package com.tanhua.model.vo;

import com.tanhua.model.mongo.Question;
import lombok.Data;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 15:00
 */
@Data
public class TestPaperVo implements Serializable {
    private String id; //问卷编号
    private String name;//问卷名称
    private String cover;//封面
    private String level;//等级
    private Integer star; //星别(2-5)
    private List<Question> questions;
    private Long updated;
    private Long created;
    private String reportId;//最新报告id
    private Integer isLock; //是否锁住（0解锁，1锁住）
}

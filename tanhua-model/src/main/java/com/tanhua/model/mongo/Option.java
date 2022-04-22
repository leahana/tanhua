package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 14:54
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option implements Serializable {
    private String id;//选项编号
    private String option; //选项内容
}

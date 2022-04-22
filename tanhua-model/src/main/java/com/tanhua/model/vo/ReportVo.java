package com.tanhua.model.vo;

import com.tanhua.model.domain.KeyValue;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 22:23
 */
@Data
public class ReportVo {

    private String conclusion;

    private String cover;

    private List<KeyValue> dimensions;

    private List<UserVo> similarYou;


}


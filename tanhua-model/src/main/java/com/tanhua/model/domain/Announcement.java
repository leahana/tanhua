package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: leah_ana
 * @Date: 2022/4/13 19:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BasePojo {
    private String id;
    private String title;
    private String description;
}

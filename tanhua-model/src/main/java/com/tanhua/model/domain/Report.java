package com.tanhua.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 15:53
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BasePojo {
    private Long id;
    private Long testPaperId;
    private Long userId;
    private Integer isLock; //是否锁住（0解锁，1锁住）
    private Integer score;

}

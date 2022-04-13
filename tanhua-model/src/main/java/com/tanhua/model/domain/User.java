package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: leah_ana
 * @Date: 2022/4/8 22:09
 */
@Data
@AllArgsConstructor //满参构造
@NoArgsConstructor  //无参构造
public class User extends BasePojo {
    private Long id;
    private String mobile;
    private String password;

    //环信用户信息
    private String hxUser;
    private String hxPassword;
}

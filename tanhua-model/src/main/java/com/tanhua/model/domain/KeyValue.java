package com.tanhua.model.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @Author: leah_ana
 * @Date: 2022/4/21 22:38
 */

@Data
public class KeyValue implements Serializable {
    private String key;
    private String value;
}

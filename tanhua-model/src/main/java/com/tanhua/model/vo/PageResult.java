package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {

    private Integer counts = 0;//总记录数
    private Integer pageSize;//页大小
    private Integer pages = 0;//总页数
    private Integer page;//当前页码
    private List<?> items = Collections.emptyList(); //列表

    public PageResult(Integer page,Integer pageSize,
                      int counts,List<?> list) {
        this.page = page;
        this.pageSize = pageSize;
        this.items = list;
        this.counts = counts;
        this.pages = counts % pageSize == 0 ? counts / pageSize : counts / pageSize + 1;
    }

}
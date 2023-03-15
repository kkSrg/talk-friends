package com.tanhua.mongo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Integer counts;
    private Integer pagesize;
    private Integer pages;
    private Integer page;
    private List<T> items;
}

/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 搜索条件请求
 *
 * @author lll 2015年11月9日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder()
public class SearchPredicate {
    // 客户端最后更新时间,0则为第一次获取,default 0
    @Min(0)
    private long updateTime = 0;
    // 分页 页码, 1-n, default 1
    @Min(1)
    private int pageNumber = 1;
    // 每页页数, default 10
    @Min(1)
    @Max(50)
    private int pageSize = 10;
    // 请求方向 默认true 向后搜索
    private boolean direction = false;

    // 排序
    private String sort = "id";

    //设备类型 0安卓 1ios
    @Min(-1)
    @Max(1)
    private int type = -1;

    public int skipNum() {
        return (this.getPageNumber() - 1) * this.getPageSize();
    }
}

/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 *
 *******************************************************************************/
package com.ming.eureka.business;


import com.ming.eureka.business.SearchFilter.Operator;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询参数列表
 *
 * @author Administrator
 */
public class SearchParam {

    private List<SearchFilter> filters = new ArrayList<SearchFilter>();

    public void add(String fieldName, Object value) {
        filters.add(new SearchFilter(fieldName, Operator.EQ, value));
    }

    public void add(String fieldName, Object value, Operator operator) {
        filters.add(new SearchFilter(fieldName, operator, value));
    }

    public List<SearchFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<SearchFilter> filters) {
        this.filters = filters;
    }
}

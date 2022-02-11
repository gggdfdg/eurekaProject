package com.ming.eureka.query;

import java.io.Serializable;
import java.util.Map;

/**
 * 辅助查询分页的对象
 */
public class QueryInfo implements Serializable {
    /**
     * action类名
     */
    private String actionClass;

    /**
     * 排序方式
     */
    private String order = "";

    /**
     * 排序名
     */
    private String orderBy = "";

    /**
     * 当前页
     */
    private int pageNumber;

    /**
     * 请求链接
     */
    private String uri;

    private Map<String, String> requestParamMap;

    public final int getPageNumber() {
        return pageNumber;
    }

    public final void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public final Map<String, String> getRequestParamMap() {
        return requestParamMap;
    }

    public final void setRequestParamMap(Map<String, String> requestParamMap) {
        this.requestParamMap = requestParamMap;
    }

    public final String getActionClass() {
        return actionClass;
    }

    public final void setActionClass(String actionClass) {
        this.actionClass = actionClass;
    }

    public final String getOrderBy() {
        return orderBy;
    }

    public final void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public final String getOrder() {
        return order;
    }

    public final void setOrder(String order) {
        this.order = order;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}

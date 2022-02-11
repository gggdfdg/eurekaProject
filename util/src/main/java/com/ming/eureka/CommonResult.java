/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 *
 *******************************************************************************/
package com.ming.eureka;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 业务处理结果
 *
 * @author lll 2015年1月29日
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CommonResult {
    private int code;
    private String msg = "操作失败";
    private Object result;
    @JsonIgnore
    private Map<String, Object> dataInfos = Maps.newHashMap();

    public CommonResult(int code, String msg) {
        super();
        this.code = code;
        if (code == ResultCode.CODE_SUCC || !StringUtils.isBlank(msg)) {
            this.msg = msg;
        }
    }

    public static CommonResult succ() {
        return new CommonResult(ResultCode.CODE_SUCC, null);
    }

    public CommonResult clearAllData() {
        this.dataInfos.clear();
        return this;
    }

    public static CommonResult notFoundError() {
        return new CommonResult(ResultCode.CODE_NOTFOUND_ERROR, ResultCode.MSG_NOTFOUND_ERROR);
    }

    public static CommonResult notFoundError(String error) {
        return new CommonResult(ResultCode.CODE_NOTFOUND_ERROR, error);
    }

    public static CommonResult createError() {
        return new CommonResult(ResultCode.CODE_CREATE_ERROR, ResultCode.MSG_CREATE_ERROR);
    }

    public static CommonResult deleError() {
        return new CommonResult(ResultCode.CODE_DELE_ERROR, ResultCode.MSG_DELE_ERROR);
    }

    public static CommonResult updateError() {
        return new CommonResult(ResultCode.CODE_UPDATE_ERROR, ResultCode.MSG_UPDATE_ERROR);
    }

    public static CommonResult repeatError() {
        return repeatError(ResultCode.MSG_REPEAT_ERROR);
    }

    public static CommonResult repeatError(String errorMsg) {
        return new CommonResult(ResultCode.CODE_REPEAT_ERROR, errorMsg);
    }

    public static CommonResult valiteError() {
        return new CommonResult(ResultCode.CODE_VALITE_ERROR, ResultCode.MSG_VALITE_ERROR);
    }

    public static CommonResult commError(String msg) {
        return new CommonResult(ResultCode.CODE_COMM_ERROR, msg);
    }

    public static CommonResult commError() {
        return new CommonResult(ResultCode.CODE_COMM_ERROR, ResultCode.MSG_COMM_ERROR);
    }

    public static CommonResult error(int code, String msg) {
        return new CommonResult(code, msg);
    }

    public static CommonResult nilError() {
        return new CommonResult(ResultCode.CODE_NIL_ERROR, ResultCode.MSG_NIL_ERROR);
    }

    public CommonResult withData(String key, Object data) {
        this.add(key, data);
        return this;
    }

    public CommonResult withData(Object data) {
        return this.withData("data", data);
    }

    public CommonResult withResult(Object result) {
        this.result = result;
        return this;
    }

    /**
     * 判断是否成功
     *
     * @return
     */
    @JsonIgnore
    public boolean isSucc() {
        return code == ResultCode.CODE_SUCC;
    }

    @JsonIgnore
    public boolean isFail() {
        return code != ResultCode.CODE_SUCC;
    }

    /**
     * 判断是否是重复调用或其他人已操作的
     *
     * @return
     */
    @JsonIgnore
    public boolean isRepeat() {
        return code == ResultCode.CODE_REPEAT_ERROR;
    }

    @JsonIgnore
    public void add(String key, Object value) {
        if (value != null) {
            dataInfos.put(key, value);
        }
    }

    @JsonIgnore
    public Map<String, Object> getDataInfos() {
        return dataInfos;
    }

    /**
     * 获取任意一个数据
     *
     * @return
     */
    @JsonIgnore
    public Object getAnyData() {
        return CollectionUtils.isEmpty(dataInfos.values()) ? null : dataInfos.values().iterator().next();

    }

    /**
     * 获取指定数据
     *
     * @param key
     * @return
     */
    @JsonIgnore
    public Object getValue(Object key) {
        if (dataInfos.containsKey(key)) {
            return dataInfos.get(key);
        } else {
            return null;
        }
    }
}

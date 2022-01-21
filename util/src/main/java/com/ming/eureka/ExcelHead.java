/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka;

/**
 * @author luoxiaomin 2016年3月15日
 */
public class ExcelHead {
    private int offset;
    private int colspan;
    private String name;

    public ExcelHead(int offset, int colspan, String name) {
        this.offset = offset;
        this.colspan = colspan;
        this.name = name;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return the colspan
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * @param colspan the colspan to set
     */
    public void setColspan(int colspan) {
        this.colspan = colspan;
    }


}

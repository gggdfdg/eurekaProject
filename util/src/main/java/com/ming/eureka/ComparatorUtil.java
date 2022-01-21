/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * @author luoxiaomin 2016年4月22日
 */
public class ComparatorUtil implements Comparator<Object> {
    private String properName;
    private boolean isAsc;

    public ComparatorUtil(String properName, boolean isAsc) {
        this.properName = properName;
        this.isAsc = isAsc;
    }

    @SuppressWarnings("unchecked")
    public int compare(Object r1, Object r2) {
        Class c = r1.getClass();
        double result = 0;
        try {
            Field field = c.getDeclaredField(properName);
            String classType = field.getType().getSimpleName();
            Method method = null;

            if ("String".equals(classType)) {
                method = c.getMethod("get" + properName.substring(0, 1).toUpperCase() + properName.substring(1), new Class[]{});
                if (isAsc) {
                    result = ((String) method.invoke(r1)).compareTo((String) method.invoke(r2));
                } else {
                    result = ((String) method.invoke(r2)).compareTo((String) method.invoke(r1));
                }

            } else if ("Integer".equals(classType) || "int".equals(classType)) {
                method = c.getMethod("get" + properName.substring(0, 1).toUpperCase() + properName.substring(1), new Class[]{});
                if (isAsc) {
                    result = ((Integer) method.invoke(r1)) - ((Integer) method.invoke(r2));
                } else {
                    result = ((Integer) method.invoke(r2)) - ((Integer) method.invoke(r1));
                }
            } else if ("Double".equals(classType) || "double".equals(classType)) {
                method = c.getMethod("get" + properName.substring(0, 1).toUpperCase() + properName.substring(1), new Class[]{});
                if (isAsc) {
                    result = ((Double) method.invoke(r1)) - ((Double) method.invoke(r2));
                } else {
                    result = ((Double) method.invoke(r2)) - ((Double) method.invoke(r1));
                }
            } else if ("Float".equals(classType) || "float".equals(classType)) {
                method = c.getMethod("get" + properName.substring(0, 1).toUpperCase() + properName.substring(1), new Class[]{});
                if (isAsc) {
                    result = ((Float) method.invoke(r1)) - ((Float) method.invoke(r2));
                } else {
                    result = ((Float) method.invoke(r2)) - ((Float) method.invoke(r1));
                }
            } else {
                result = -100;
                throw new Exception("不支持的数据类型");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 确定返回值  
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        }
        return 0;
    }


    /**
     * @return the properName
     */
    public String getProperName() {
        return properName;
    }

    /**
     * @param properName the properName to set
     */
    public void setProperName(String properName) {
        this.properName = properName;
    }

    /**
     * @return the isAsc
     */
    public boolean getIsAsc() {
        return isAsc;
    }

    /**
     * @param isAsc the isAsc to set
     */
    public void setIsAsc(boolean isAsc) {
        this.isAsc = isAsc;
    }


}

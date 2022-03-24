/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 *
 *******************************************************************************/
package com.ming.eureka;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 键值对象
 *
 * @author lll 2015年3月6日
 */
public class KeyValueObj<K, V> {
    private K key;
    private V value;

    public KeyValueObj(K key, V value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(V value) {
        this.value = value;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.key.equals(((KeyValueObj<K, V>) obj).getKey());
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "key");
    }
}

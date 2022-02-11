//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ming.eureka;

import com.google.common.collect.Lists;
import org.dozer.DozerBeanMapper;
import java.util.*;

/**
 * 对象拷贝
 */
public class BeanMapper {
    private static DozerBeanMapper dozer = new DozerBeanMapper();

    public BeanMapper() {
    }

    public static <T> T map(Object source, Class<T> destinationClass) {
        return dozer.map(source, destinationClass);
    }

    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        List<T> destinationList = Lists.newArrayList();
        Iterator var3 = sourceList.iterator();

        while (var3.hasNext()) {
            Object sourceObject = var3.next();
            T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }

        return destinationList;
    }

    public static void copy(Object source, Object destinationObject) {
        dozer.map(source, destinationObject);
    }

    public static void main(String args[]){
        //深拷贝对象，将pp的对象属性拷贝到zz，pp对象和zz的属性名字以一定不要带二维的list，并且属性名要一样
        //copy(pp, zz);
        //深拷贝对象，将pp的对象属性拷贝到t，pp对象和ZZ的属性名字以一定不要带二维的list，并且属性名要一样
        //ZZ t = map(PP,ZZ.class);
    }

}

package com.ming.eureka.model.entity.file;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Set;

/**
 * 图片来源枚举常量
 * 使用与类名一样的枚举名称
 *
 * @author lll 2015年7月14日
 */
public enum SourceType {
    Media,
    ImageMaterial,
    PortraitMaterial,
    BackgroundMaterial,
    VoiceMaterial,
    QRcodeMaterial,
    AutoReplyMaterial,
    AppletMaterial,
    PddCommentImg,
    GroupsMaterial,
    QqKSongProductionMaterial;

    /**
     * 获取文件路径
     *
     * @param dir
     * @return
     */
    public String pathInDir(String dir) {
        if (dir.endsWith(File.separator)) {
            return StringUtils.join(dir, this.name());
        }
        return StringUtils.join(dir, File.separator, this.name());
    }

    /**
     * 判断该类型资源是否是存储图片
     *
     * @return
     */
    static Set<SourceType> noImageTypes = Sets.newHashSet();

    public boolean isImage() {
        return !noImageTypes.contains(this);
    }

    public Class<?> getFileClass() {
        try {
            return Class.forName(StringUtils.join("com.ming.eureka.model.entity.", this.name()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据类型创建对象
     *
     * @return
     */
    public AbstractFile createObject() {
        Object file = null;
        try {
            Class<?> fileClass = this.getFileClass();
            file = fileClass.newInstance();
            if (file.getClass().getSuperclass() == AbstractFile.class) {
                return (AbstractFile) file;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}

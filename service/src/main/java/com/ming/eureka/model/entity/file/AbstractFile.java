package com.ming.eureka.model.entity.file;

import com.google.common.collect.Lists;
import com.ming.eureka.FileUtil;
import com.ming.eureka.model.entity.IdTimeTenantEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.springframework.scheduling.annotation.Async;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 抽象文件实体
 *
 * @author lll 2015年7月14日
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AbstractFile extends IdTimeTenantEntity {

    public static final String SPEC_VIDEO_FACE = "vface";//视频封面
    public static final String SPEC_ORIGNAL = "0";// 原图

    /**
     * 文件存储相对路径
     */
    private String uri;

    /**
     * 文件名
     */
    private String name;

    // 文件格式
    private String contentType;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 规格数组
     */
    private String specs;

    public SourceType getSourceType() {
        return SourceType.valueOf(this.getClass().getSimpleName());
    }

    /**
     * 返回文件url
     *
     * @param spec
     * @return
     */
    public String getFileUrl(String spec) {
        return AbstractFile.getFileUrl(id, getSourceType(), this.getUpdateTime(), spec);
    }

    public static String getFileUrl(long id, SourceType type, Date updateTime, String spec) {
        if (StringUtils.isBlank(spec)) {
            spec = SPEC_ORIGNAL;
        }
        String url = "";
        return StringUtils.join(url, "/files/", type.name(), "/", String.valueOf(id), "/",
                spec, "?t=", String.valueOf(updateTime.getTime() / 1000));
    }

    /**
     * 原图url
     *
     * @return
     */
    @Transient
    public String getFileUrl() {
        return this.getFileUrl(null);
    }

    /**
     * 1*1024*1024url
     *
     * @return
     */
    @Transient
    public String get1024FileUrl() {
        return this.getFileUrl("1x1024x1024");
    }

    /**
     * 删除图片url
     *
     * @return
     */
    @Transient
    public String getDeleteUrl() {
        String url = "";
        return StringUtils.join(url, "/files/delete/", this.getClass().getSimpleName(), "/", String.valueOf(this.getId()));
    }

    /**
     * 获取第一个缩略图规格,没有则返回原图
     *
     * @return
     */
    public String getThumbUrl() {
        if (StringUtils.isBlank(this.getUri())) {
            return "";
        }
        String firstSpec = CollectionUtils.isEmpty(this.getSpecList()) ? null : this.getSpecList().iterator().next();
        return this.getFileUrl(firstSpec);
    }

    public String getThumbUrl(String spec) {
        return AbstractFile.getFileUrl(id, getSourceType(), this.getUpdateTime(), spec);
    }

    /**
     * 获取所有缩略图链接（含原图）
     *
     * @return
     */
    public List<String> getImageUrls() {
        List<String> imageUrls = Lists.newArrayList();
        imageUrls.add(this.getFileUrl());
        for (String spec : this.getSpecList()) {
            imageUrls.add(this.getFileUrl(spec));
        }
        return imageUrls;
    }

    /**
     * 获取所有缩略图链接（含原图）
     *
     * @return
     */
    public static List<String> getImageUrls(long id, SourceType type, String specs, Date updateTime) {
        List<String> imageUrls = Lists.newArrayList();
        List<String> specList = null;
        if (StringUtils.isBlank(specs)) {
            specList = Arrays.asList();
        } else {
            specList = Arrays.asList(specs.split(","));
        }

        imageUrls.add(getFileUrl(id, type, updateTime, null));
        for (String spec : specList) {
            imageUrls.add(getFileUrl(id, type, updateTime, spec));
        }
        return imageUrls;
    }

    /**
     * 获取所有缩略图链接（不含原图）
     *
     * @return
     */
    public List<String> getThumbUrls() {
        List<String> thumbUrls = Lists.newArrayList();
        for (String spec : this.getSpecList()) {
            thumbUrls.add(this.getFileUrl(spec));
        }
        return thumbUrls;
    }

    /**
     * 获取文件绝对路径
     *
     * @param diskDir
     * @param spec
     * @return
     */
    public String getAbsolutePathInDisk(String diskDir, String spec) {
        Asserts.notBlank(diskDir, "diskDir 不能为空！");
        String diskDirT = diskDir;
        if (!diskDirT.endsWith(File.separator)) {
            diskDirT = StringUtils.join(diskDir, File.separator);
        }
        if (StringUtils.isNotBlank(spec)) {
            String descDir = StringUtils.substringBeforeLast(uri, File.separator);
            String fileName = StringUtils.substringAfterLast(uri, File.separator);
            return StringUtils.join(diskDirT, descDir, File.separator, spec, File.separator, fileName);
        } else {
            return StringUtils.join(diskDirT, uri);
        }
    }

    /**
     * 删除文件
     */
    @Async
    public void removeAllFileInDisk(String diskDir) {
        String diskDirT = diskDir;
        if (diskDirT.endsWith(File.separator)) {
            diskDirT = StringUtils.join(diskDir, File.separator);
        }
        FileUtil.deleteFile(StringUtils.join(diskDirT, uri));
        String descDir = StringUtils.substringBeforeLast(uri, File.separator);
        String fileName = StringUtils.substringAfterLast(uri, File.separator);

        for (String spec : this.getSpecList()) {
            FileUtil.deleteFile(StringUtils.join(diskDirT, descDir, File.separator, spec, File.separator, fileName));
        }
    }

    public List<String> getSpecList() {
        if (StringUtils.isBlank(specs)) {
            return Arrays.asList();
        }
        return Arrays.asList(specs.split(","));
    }

}
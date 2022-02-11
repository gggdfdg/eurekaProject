/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.entity.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.ming.eureka.BeanMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.File;
import java.util.Collection;
import java.util.Date;

/**
 * 图片dto
 *
 * @author lll 2015年7月27日
 */
public class ImagesDto {
    private int number;
    private int totalPages;//页数
    private long totalElements;//个数

    @Mapping("content")
    private Collection<ImageDto> files;

    public ImagesDto() {
        super();
    }

    public ImagesDto(Page<? extends AbstractFile> page) {
        super();
        this.number = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();

        this.files = Collections2.transform(page.getContent(), new Function<AbstractFile, ImageDto>() {
            @Override
            public ImageDto apply(AbstractFile file) {
                return new ImageDto(file);
            }
        });

    }

    public static class ImageDto {
        private long id;
        private String name;//文件名
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+08:00")
        private Date createTime;
        //原图链接
        private String url;
        //第一张缩略图链接（一般为最小图片）
        private String thumbnailUrl;
        //删除图片链接
        private String deleteUrl;
        // 第二张图片
        private String secondUrl;
        //文字消息内容
        private String txtMessage;

        public ImageDto(AbstractFile file) {
            super();
            BeanMapper.copy(file, this);
            if (file.getId() != null && file.getId() > 0) {
                this.url = file.getFileUrl();
                this.deleteUrl = file.getDeleteUrl();
                String fileUtil = CollectionUtils.isEmpty(file.getSpecList()) ? null : file.getSpecList().iterator().next();
                this.thumbnailUrl = file.getFileUrl(fileUtil);
                if (file.getSpecList().size() > 1) {
                    this.secondUrl = file.getFileUrl(file.getSpecList().get(1));
                }
            } else {
                this.url = file.getUri().replaceAll("\\\\", "/") + "|" + file.getContentType() + "|" + file.getSize();
                String spec = "0";
                if (CollectionUtils.isNotEmpty(file.getSpecList())) {
                    String specList = CollectionUtils.isEmpty(file.getSpecList()) ? null : file.getSpecList().iterator().next();
                    spec = String.valueOf(specList);
                }
                this.thumbnailUrl = StringUtils.join("/files", File.separator, file.getUri().replaceAll("\\\\", "/"),
                        "/" + spec,
                        "?t=", String.valueOf(new Date().getTime() / 1000), "&content=" + file.getContentType());
            }
        }

        /**
         * @return the secondUrl
         */
        public String getSecondUrl() {
            return secondUrl;
        }

        /**
         * @param secondUrl the secondUrl to set
         */
        public void setSecondUrl(String secondUrl) {
            this.secondUrl = secondUrl;
        }

        /**
         * @return the thumbnailUrl
         */
        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        /**
         * @param thumbnailUrl the thumbnailUrl to set
         */
        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
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
         * @return the createTime
         */
        public Date getCreateTime() {
            return createTime;
        }

        /**
         * @param createTime the createTime to set
         */
        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url the url to set
         */
        public void setUrl(String url) {
            this.url = StringUtils.join(url);
        }

        /**
         * @return the id
         */
        public long getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(long id) {
            this.id = id;
        }

        /**
         * @return the deleteUrl
         */
        public String getDeleteUrl() {
            return deleteUrl;
        }

        /**
         * @param deleteUrl the deleteUrl to set
         */
        public void setDeleteUrl(String deleteUrl) {
            this.deleteUrl = deleteUrl;
        }

        public String getTxtMessage() {
            return txtMessage;
        }

        public void setTxtMessage(String txtMessage) {
            this.txtMessage = txtMessage;
        }
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return the totalPages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages the totalPages to set
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return the totalElements
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * @param totalElements the totalElements to set
     */
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * @return the files
     */
    public Collection<ImageDto> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(Collection<ImageDto> files) {
        this.files = files;
    }


}

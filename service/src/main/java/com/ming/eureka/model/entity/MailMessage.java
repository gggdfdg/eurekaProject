/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.ming.eureka.model.entity;

import java.io.File;
import java.util.List;

/**
 * 邮件消息
 *
 * @author lll 2015年5月28日
 */
public class MailMessage extends Message {

    private List<String> recvMails;//收件人邮箱地址
    private String subject; // 主题
    private File attachment;//附件

    /**
     * @param subject
     * @param content
     * @param attachment
     */
    public MailMessage(List<String> recvMails, String subject,
                       String content, File attachment) {
        super();
        this.recvMails = recvMails;
        this.attachment = attachment;
        this.subject = subject;
        this.setContent(content);
    }

    public List<String> getRecvMails() {
        return recvMails;
    }

    public void setRecvMails(List<String> recvMails) {
        this.recvMails = recvMails;
    }

    /**
     * @return the attachment
     */
    public File getAttachment() {
        return attachment;
    }

    /**
     * @param attachment the attachment to set
     */
    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

}

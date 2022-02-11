package com.ming.eureka.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ming.eureka.model.entity.file.AbstractFile;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.Type;

@Getter@Setter
@Entity
@Table(name = "b_media")
public class Media extends AbstractFile {
	
	// 唯一标识
	@Column(name = "sid", unique = true, length = 100)
	private String sid;
	// 源链接
	@Type(type = "text")
	private String url;
	
	public boolean isDownload() {
		return this.getUri() != null;
	}
	
	public void setUrl(String url) {
		this.url = url;
		this.sid = DigestUtils.md5Hex(url);
	}

	@Override
	public void prepareForInsert() {
		
	}

}

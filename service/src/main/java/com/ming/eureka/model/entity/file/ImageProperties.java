/**
 * 
 */
package com.ming.eureka.model.entity.file;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 图片属性
 */
@Component
public class ImageProperties {

	/** 头像图片规格 */
	@Value("#{'${image.specs.user_avatar}'.split(',')}")
	public List<String> userAvatars = new ArrayList<String>();
	@Value("#{'${image.specs.media}'.split(',')}")
	public List<String> medias = new ArrayList<String>();
	@Value("#{'${image.specs.image_material}'.split(',')}")
	public List<String> imageMaterial = new ArrayList<String>();
	@Value("#{'${image.specs.portrait_material}'.split(',')}")
	public List<String> portraitMaterial = new ArrayList<String>();
	@Value("#{'${image.specs.background_material}'.split(',')}")
	public List<String> backgroundMaterial = new ArrayList<String>();

	@Value("#{'${image.specs.background_material}'.split(',')}")
	public List<String> QRcodeMaterial = new ArrayList<String>();
	
	@Value("#{'${image.specs.autoreply_material}'.split(',')}")
	public List<String> AutoReplyMaterial = new ArrayList<String>();

	@Value("#{'${image.specs.applet_material}'.split(',')}")
	public List<String> AppletMaterial = new ArrayList<String>();
	@Value("#{'${image.specs.pdd_comment_img}'.split(',')}")
	public List<String> PddCommentImg = new ArrayList<String>();

	@Value("#{'${image.specs.group_material}'.split(',')}")
	public List<String> groupMaterial = new ArrayList<String>();

	/**
	 * 获取图片规格
	 * 
	 * @param customSpecs
	 * @param sourceType
	 * @return specs 为空 则按配置文件，返回图片规格，
	 */
	public List<String> getSpecs(String customSpecs, SourceType sourceType) {

		List<String> imagespecs = Lists.newArrayList();

		// 头像
		// TODO IMAGE
		if (sourceType == SourceType.Media) {
			imagespecs.addAll(this.medias);
		} else if (sourceType == SourceType.ImageMaterial) {
			imagespecs.addAll(this.imageMaterial);
		} else if (sourceType == SourceType.PortraitMaterial) {
			imagespecs.addAll(this.portraitMaterial);
		} else if (sourceType == SourceType.BackgroundMaterial) {
			imagespecs.addAll(this.backgroundMaterial);
		} else if (sourceType == SourceType.VoiceMaterial) {
		} else if (sourceType == SourceType.QRcodeMaterial) {
			imagespecs.addAll(this.QRcodeMaterial);
		}else if (sourceType == SourceType.AutoReplyMaterial) {
			imagespecs.addAll(this.AutoReplyMaterial);
		}else if (sourceType == SourceType.AppletMaterial) {
			imagespecs.addAll(this.AppletMaterial);
		}else if (sourceType == SourceType.PddCommentImg) {
			imagespecs.addAll(this.PddCommentImg);
		} else if (sourceType == SourceType.GroupsMaterial) {
			imagespecs.addAll(this.groupMaterial);
		} else if (sourceType == SourceType.QqKSongProductionMaterial) {
		} else {
			Asserts.check(false, "未设置相应规格配置 @see ImageProperties.java ");
		}

		if (StringUtils.isNotBlank(customSpecs)) {
			imagespecs.addAll(Arrays.asList(customSpecs.split(",")));
		}

		return imagespecs;
	}
	
}

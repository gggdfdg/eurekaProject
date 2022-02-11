package com.ming.eureka;

/**
 * 图片裁切常量
 *
 * @author lll 2015年7月14日
 */
public interface GraphicImageConstants {

    /**
     * 全局工具路径 linux环境下不需要此路径
     */
    String GLOBLE_PATH = "G:\\eurekaProject\\tools\\GraphicsMagick-1.3.33-Q16\\";

    /**
     * 水印内容
     */
    String WATERMARK = "www.wdd.com";

    /**
     * 水印格式，位置起点
     */
    String MARK_PARAM = "text 0,0 ";

    /**
     * 是否使用gm处理图片，false/null:使用im处理图片
     */
    Boolean GM = true;

    /**
     * 水印字体
     */
    String FONT = "Arial";

    /**
     * 水印颜色
     */
    String COLOR = "yellow";

    /**
     * 透明度
     */
    Integer DISSOLVE = 50;

    /**
     * 水印重心
     */
    String GRAVITY = "southeast";

    /**
     * 字体粗度
     */
    Integer POINTSIZE = 50;

}

/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 *
 *******************************************************************************/
package com.ming.eureka;

import org.im4java.core.*;
import org.im4java.process.ArrayListOutputConsumer;
import org.im4java.process.ProcessStarter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片处理工具类
 * 旋转啥的
 *
 * @author lll 2015年7月15日
 */
public class ImageUtil {

    /**
     * 创建色图
     *
     * @param color
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage createColorImage(Color color, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setBackground(color);
        g2.clearRect(0, 0, width, height);
        return bi;
    }

    /**
     * 图片压缩 照宽高比例不变
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void resizeImageKeepScale(String srcPath, Integer nWidth,
                                            Integer nHeight, BufferedImage img, String descPath) throws IOException,
            InterruptedException, IM4JavaException {

        int height = img.getHeight();
        int width = img.getWidth();

        if (nHeight >= height) {
            nHeight = height;
        }

        if (nWidth >= width) {
            nWidth = width;
        }

        GMOperation op = new GMOperation();
        op.addImage();
        op.addRawArgs("-quality", "100.0");
        op.resize(nWidth, nHeight);
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addImage();
        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 图片压缩 严格按照指定规格压缩图片
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void resizeImage(String srcPath, Integer nWidth, Integer nHeight,
                                   BufferedImage img, String descPath) throws IOException, InterruptedException, IM4JavaException {
        int height = img.getHeight();
        int width = img.getWidth();

        if (nHeight >= height) {
            nHeight = height;
        }

        if (nWidth >= width) {
            nWidth = width;
        }

        IMOperation op = new IMOperation();

        op.addImage();
        op.resize(nWidth, nHeight, "!");
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 固定宽度缩放图片
     *
     * @param srcPath
     * @param nWidth
     * @param img
     * @param descPath
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void resizeImageFixWidth(String srcPath, Integer nWidth,
                                           BufferedImage img, String descPath) throws IOException, InterruptedException, IM4JavaException {

        IMOperation op = new IMOperation();

        op.addImage();
        op.resize(nWidth, null);
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "75.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 固定高度缩放图片
     *
     * @param srcPath
     * @param nHeight
     * @param img
     * @param descPath
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void resizeImageFixHeight(String srcPath, Integer nHeight,
                                            BufferedImage img, String descPath) throws IOException, InterruptedException, IM4JavaException {

        IMOperation op = new IMOperation();

        op.addImage();
        op.resize(null, nHeight);
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "75.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 图片先压缩后居中裁切
     *
     * @param eWidth  裁切宽度
     * @param eHeight 裁切高度
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void pressAndCutImage(String srcPath, Integer nWidth, Integer nHeight,
                                        Integer eWidth, Integer eHeight, BufferedImage img, String descPath) throws IOException,
            InterruptedException, IM4JavaException {

        int height = img.getHeight();
        int width = img.getWidth();

        if (nHeight >= height) {
            nHeight = height;
        }

        if (nWidth >= width) {
            nWidth = width;
        }

        IMOperation op = new IMOperation();

        op.addImage();
        op.resize(nWidth, nHeight, "^").crop(eWidth, eHeight, 0, (nHeight - eHeight) / 2);
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 图片先裁切后压缩
     *
     * @param eWidth  裁切宽度
     * @param eHeight 裁切高度
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void cutAndPressImage(String srcPath, Integer eWidth, Integer eHeight, Integer nWidth, Integer nHeight, BufferedImage img, String descPath) throws IOException,
            InterruptedException, IM4JavaException {
        int height = img.getHeight();
        int width = img.getWidth();

        if (eHeight >= height) {
            eHeight = height;
        }

        if (eWidth >= width) {
            eWidth = width;
        }

        IMOperation op = new IMOperation();

        op.addImage();
        op.extent(eWidth, eHeight).resize(nWidth, nHeight, "^");
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 头像处理 先压缩再裁剪
     *
     * @param rectw
     * @param recth
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void cropImageCenter(String srcPath, BufferedImage img, String descPath, int rectw, int recth) throws IOException, InterruptedException, IM4JavaException {
        IMOperation op = new IMOperation();

        int height = img.getHeight();
        int width = img.getWidth();

        if (recth > height) {
            rectw = (int) (rectw * ((double) height / recth));
            recth = height;
        }

        if (rectw > width) {
            recth = (int) (recth * ((double) width / rectw));
            rectw = width;
        }

        op.addImage();
        op.autoOrient().resize(rectw, recth, '^').gravity("center").extent(rectw, recth).quality(100d);
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 图片裁切 根据坐标处理图片
     *
     * @param x  起点横坐标
     * @param y  起点纵坐标
     * @param x1 终点横坐标
     * @param y1 终点纵坐标
     * @throws Exception
     */
    public static void cutImageByPoint(String srcPath, BufferedImage img, String descPath, Integer x, Integer y, Integer x1, Integer y1) throws Exception {

        int nWidth = x1 - x;
        int nHeight = y1 - y;
        cutImageByPointAndWH(srcPath, img, descPath, nWidth, nHeight, x, y);
    }

    /**
     * 图片裁切 根据坐标,指定宽高处理图片
     *
     * @param x 起点横坐标
     * @param y 起点纵坐标
     * @throws Exception
     */
    public static void cutImageByPointAndWH(String srcPath, BufferedImage img, String descPath, Integer nWidth, Integer nHeight, Integer x, Integer y) throws Exception {

        IMOperation op = new IMOperation();

        int height = img.getHeight();
        int width = img.getWidth();
        if (nHeight >= height) {
            nHeight = height;
        }

        if (nWidth >= width) {
            nWidth = width;
        }

        op.addImage();
        op.crop(nWidth, nHeight, x, y);
        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 先根据坐标裁切,再进行压缩
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param eWidth
     * @param eHeight
     * @throws Exception
     */
    public static void cutImageByPointAndPress(String srcPath, BufferedImage img, String descPath,
                                               Integer x, Integer y, Integer x1, Integer y1,
                                               Integer eWidth, Integer eHeight)
            throws Exception {

        int nWidth = x1 - x;
        int nHeight = y1 - y;

        cutImageByPointWHAndPress(srcPath, img, descPath, x, y, nWidth, nHeight, eWidth, eHeight);

    }

    /**
     * 先根据坐标和指定宽高裁切,再进行压缩
     *
     * @param x
     * @param y
     * @param nWidth
     * @param nHeight
     * @param eWidth
     * @param eHeight
     * @throws Exception
     */
    public static void cutImageByPointWHAndPress(String srcPath, BufferedImage img, String descPath, Integer x, Integer y, Integer nWidth, Integer nHeight,
                                                 Integer eWidth, Integer eHeight)
            throws Exception {

        int height = img.getHeight();
        int width = img.getWidth();
        if (nHeight >= height) {
            nHeight = height;
        }

        if (nWidth >= width) {
            nWidth = width;
        }

        IMOperation op = new IMOperation();

        op.addImage();
        op.crop(nWidth, nHeight, x, y).resize(eWidth, eHeight);

        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();
        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 给图片添加文字水印
     *
     * @param markparam
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void addWordWaterMark(String srcPath, BufferedImage img, String descPath, String markparam)
            throws IOException, InterruptedException, IM4JavaException {

        IMOperation op = new IMOperation();

        op.addImage();
        op.font(GraphicImageConstants.FONT);
        op.fill(GraphicImageConstants.COLOR);
        op.pointsize(GraphicImageConstants.POINTSIZE);
        op.gravity(GraphicImageConstants.GRAVITY);
        op.dissolve(GraphicImageConstants.DISSOLVE);
        op.draw(markparam + GraphicImageConstants.WATERMARK);

        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, srcPath, descPath);
    }

    /**
     * 给图片添加图片水印
     *
     * @param waterPic 水印图片
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void addImageWaterMark(String srcPath, String waterPic, BufferedImage img, String descPath) throws IOException, InterruptedException, IM4JavaException {

        IMOperation op = new IMOperation();

        op.addImage(waterPic);
        op.gravity(GraphicImageConstants.GRAVITY);
        op.dissolve(GraphicImageConstants.DISSOLVE);

        //去掉图片所有内置信息
        op.addRawArgs("+profile", "*");
        op.addRawArgs("-quality", "90.0");
        op.addImage();

        CompositeCmd cmd = createCompositeCmd();
        cmd.run(op, img, descPath);

    }

    /**
     * 处理原图
     * 重置图片旋转方向等
     *
     * @param path
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static void dealAndSaveImage(Object path, Object descPath) throws IOException, InterruptedException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.addImage();
        op.autoOrient();
        op.addRawArgs("+profile", "*");
        op.addImage();

        ConvertCmd cmd = createConvertCmd();
        cmd.run(op, path, descPath);
    }

    /**
     * 获取图片基本信息
     *
     * @param is
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static List<Integer> getImageInfo(InputStream is) throws IOException, InterruptedException, IM4JavaException {

        List<Integer> imagewh = new ArrayList<Integer>();

        IMOperation op = new IMOperation();
        op.format("%w,%h");// ,path:%d%f,size:%b%[EXIF:DateTimeOriginal]
        op.addImage(1);
        IdentifyCmd cmd = new IdentifyCmd(GraphicImageConstants.GM);

        ArrayListOutputConsumer output = new ArrayListOutputConsumer();
        BufferedImage img = ImageIO.read(is);
        cmd.setOutputConsumer(output);
        cmd.run(op, img);

        ArrayList<String> cmdOutput = output.getOutput();
        String[] line = cmdOutput.get(0).split(",");
        Integer width = Integer.valueOf(line[0]);
        Integer height = Integer.valueOf(line[1]);
        imagewh.add(width);
        imagewh.add(height);
        return imagewh;
    }

    public static ConvertCmd createConvertCmd() {
        ConvertCmd cmd = new ConvertCmd(GraphicImageConstants.GM);
        if (!isWindows) {
            cmd.setSearchPath("/usr/local/bin/");
        }
        return cmd;
    }

    public static CompositeCmd createCompositeCmd() {
        CompositeCmd cmd = new CompositeCmd(GraphicImageConstants.GM);
        if (!isWindows) {
            cmd.setSearchPath("/usr/local/bin/");
        }
        return cmd;
    }

    public static boolean isWindows = false;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("win") >= 0) { // linux下不要设置此值，不然会报错
            isWindows = true;
            ProcessStarter.setGlobalSearchPath(GraphicImageConstants.GLOBLE_PATH);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException, IM4JavaException {
        ImageUtil.dealAndSaveImage("/var/wyx/product/BuyerAvatar/2015-09-23/12/14/2EF8E812F3877C40C8638DD1B0B81B71.jpg", "/Users/lll/1.jpg");
    }
}

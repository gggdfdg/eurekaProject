
package com.ming.eureka;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成(二维码里面可以内嵌图片)和解析
 *
 * @author lichunxi
 */
public class BarcodeFactory {
    // 嵌入的图片宽度
    private static final int IMAGE_WIDTH = 30;
    private static final int IMAGE_HEIGHT = 30;
    private static final int IMAGE_HALF_WIDTH = IMAGE_WIDTH / 2;
    private static final int FRAME_WIDTH = 2;

    // 二维码写码器
    private static MultiFormatWriter mutiWriter = new MultiFormatWriter();

    /**
     * 生成二维码
     *
     * @param content   内容
     * @param width     宽度
     * @param height    高度
     * @param srcImage  内嵌图片
     * @param destImage 目标图片
     */
    public static void encode(String content, int width, int height,
                              File srcImage, File destImage) {
        //文件存在则不生成，不存在则生成
        if (!destImage.exists()) {
            try {
                ImageIO.write(genBarcode(content, width, height, srcImage),
                        "png", destImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static BufferedImage genBarcode(String content, int width,
                                            int height, File srcImage) throws WriterException,
            IOException {
        int[][] srcPixels = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        boolean needImage = srcImage != null;
        if (srcImage != null) {
            // 读取源图像
            BufferedImage scaleImage = scale(srcImage, IMAGE_WIDTH,
                    IMAGE_HEIGHT, true);
            for (int i = 0; i < scaleImage.getWidth(); i++) {
                for (int j = 0; j < scaleImage.getHeight(); j++) {
                    srcPixels[i][j] = scaleImage.getRGB(i, j);
                }
            }
        }


        Map<EncodeHintType, Object> hint = new HashMap<EncodeHintType, Object>();
        hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 生成二维码
        BitMatrix matrix = mutiWriter.encode(content, BarcodeFormat.QR_CODE,
                width, height, hint);

        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int[] pixels = new int[width * height];

        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                if (needImage) {
                    // 读取图片
                    if (x > halfW - IMAGE_HALF_WIDTH
                            && x < halfW + IMAGE_HALF_WIDTH
                            && y > halfH - IMAGE_HALF_WIDTH
                            && y < halfH + IMAGE_HALF_WIDTH) {
                        pixels[y * width + x] = srcPixels[x - halfW
                                + IMAGE_HALF_WIDTH][y - halfH + IMAGE_HALF_WIDTH];
                    }
                    // 在图片四周形成边框
                    else if ((x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                            && x < halfW - IMAGE_HALF_WIDTH + FRAME_WIDTH
                            && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                            + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                            || (x > halfW + IMAGE_HALF_WIDTH - FRAME_WIDTH
                            && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                            && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                            + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                            || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                            && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                            && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                            - IMAGE_HALF_WIDTH + FRAME_WIDTH)
                            || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                            && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                            && y > halfH + IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                            + IMAGE_HALF_WIDTH + FRAME_WIDTH)) {
                        pixels[y * width + x] = 0xfffffff;
                    } else {
                        // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                        pixels[y * width + x] = matrix.get(x, y) ? 0xff000000
                                : 0xfffffff;
                    }
                } else {
                    // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * width + x] = matrix.get(x, y) ? 0xff000000
                            : 0xfffffff;
                }

            }
        }

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);

        return image;
    }

    /**
     * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
     *
     * @param srcImageFile 源文件
     * @param height       目标高度
     * @param width        目标宽度
     * @param hasFiller    比例不对时是否需要补白：true为补白; false为不补白;
     * @throws IOException
     */
    private static BufferedImage scale(File srcImageFile, int height,
                                       int width, boolean hasFiller) throws IOException {
        double ratio = 0.0; // 缩放比例

        BufferedImage srcImage = ImageIO.read(srcImageFile);
        Image destImage = srcImage.getScaledInstance(width, height,
                BufferedImage.SCALE_SMOOTH);
        // 计算比例
        if ((srcImage.getHeight() > height) || (srcImage.getWidth() > width)) {
            if (srcImage.getHeight() > srcImage.getWidth()) {
                ratio = (new Integer(height)).doubleValue()
                        / srcImage.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue()
                        / srcImage.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(
                    AffineTransform.getScaleInstance(ratio, ratio), null);
            destImage = op.filter(srcImage, null);
        }
        if (hasFiller) {// 补白
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = image.createGraphics();
            graphic.setColor(Color.white);
            graphic.fillRect(0, 0, width, height);
            if (width == destImage.getWidth(null))
                graphic.drawImage(destImage, 0,
                        (height - destImage.getHeight(null)) / 2,
                        destImage.getWidth(null), destImage.getHeight(null),
                        Color.white, null);
            else
                graphic.drawImage(destImage,
                        (width - destImage.getWidth(null)) / 2, 0,
                        destImage.getWidth(null), destImage.getHeight(null),
                        Color.white, null);
            graphic.dispose();
            destImage = image;
        }
        return (BufferedImage) destImage;
    }

    /**
     * 针对二维码进行解析
     *
     * @param imgPath
     * @return
     */
    public static String decodePR(String imgPath) {
        BufferedImage image = null;
        Result result = null;
        try {
            image = ImageIO.read(new File(imgPath));
            if (image == null) {
                System.out.println("the decode image may be not exists.");
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

            result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        //二维码隐藏的信息
        String content = "很棒";
        //二维码图片宽度
        int width = 300;
        //二维码图片高度
        int height = 300;
        //嵌入二维码中间的图片路径(如果中间不需要嵌入个人图片，直接传null)
        File embeddedPngFile = new File("G:\\2013.png");
        //生成的二维码图片路径
        String destImg = "G:\\2013-01.png";
        //生成二维码图片
        BarcodeFactory.encode(content,
                width, height, embeddedPngFile, new File(destImg));
        //解码二维码信息
        System.out.println(BarcodeFactory.decodePR("G:\\2013-01.png"));
    }

}

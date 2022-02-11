//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_4.1.1/xslt/JavaClass.xsl

package com.ming.eureka;

import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 为登录模块生成验证码
 *
 * @author ziegler
 */
public class ValidateCodeUtils {

    private static Map<String, String> TYPE = new HashMap<String, String>();

    private static ValidateCodeUtils instance = new ValidateCodeUtils();

    public static ValidateCodeUtils getInstance() {
        return instance;
    }

    private ValidateCodeUtils() {
    }

    /**
     * @param key 验证码的 paramet type 和 session key
     */
    public void setTYPE(String key) {
        setTYPE(key, key);
    }

    /**
     * @param key   验证码的 paramet type
     * @param value 验证吗的 session key
     */
    public void setTYPE(String key, String value) {
        TYPE.put(key, value);
    }

    /**
     * 生成随机字符串
     *
     * @return
     */
    public String getRandomString() {
        String validate_code = "";

        Random random = new Random();
        int i;
        for (i = 0; i < 4; i++) {
            validate_code += String.valueOf(random.nextInt(10));
        }

        // 取得一个4位随机字母数字字符串

        String s = RandomStringUtils.random(4, true, false);

        return s.toUpperCase();
    }

    /**
     * 生成验证码  Method execute
     *
     * @return ActionForward
     */
    public void string2img(String str, OutputStream outputSteam) {

        try {
            ImageIO.write(generateImageFromString(str), "JPEG", outputSteam);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成字符串对应的验证码框,
     *
     * @param validate_code
     * @return
     */
    private BufferedImage generateImageFromString(String validate_code) {
        int width = 53, height = 19;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取图形上下文

        Graphics2D g = image.createGraphics();
        // 生成随机类
        Random random = new Random();

        // 设定背景色

        g.setColor(getRandColor(240, 250));
        g.fillRect(0, 0, width, height);

        // 设定字体
        g.setFont(new Font("DialogInput", Font.ITALIC, 20));

        // 随机产生255条干扰线，使图象中的认证码不易被其它程序探测到

        g.setColor(getRandColor(160, 180));
        for (int i = 0; i < 10; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

//	  旋转
        g.rotate(0.09234222, 25, -20);
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            //g.drawString(s.substring(i, i + 1), 13 * i +1, 16);
            g.drawString(validate_code.substring(i, i + 1), 13 * i + 1, 16);
            if (i == 0)
                g.rotate(-0.1523411, 40, 20);
            else if (i == 1)
                g.rotate(0.5034111, 40, 15);
            else if (i == 2)
                g.rotate(-0.70234222, 40, 10);
        }

        // 图象生效
        g.dispose();

        return image;
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) { // 给定范围获得随机颜色
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}

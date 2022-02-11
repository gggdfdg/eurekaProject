package com.ming.eureka;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * 视频相关
 *
 * @author lll
 */
public class VideoUtil {

    /**
     * 生成封面文件
     *
     * @param videoFilePath
     * @param descPath
     * @return
     */
    @SneakyThrows
    public static File generateVideoFace(String videoFilePath, String descPath) {
        @Cleanup("stop") FFmpegFrameGrabber ffg = new FFmpegFrameGrabber(videoFilePath);
        ffg.start();
        Frame frame = ffg.grabImage();
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bi = converter.getBufferedImage(frame);
        File output = new File(descPath);
        ImageIO.write(bi, "jpg", output);
        return output;
    }

}

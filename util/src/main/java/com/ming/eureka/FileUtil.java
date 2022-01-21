/*******************************************************************************
 * Copyright (c) 2005, 2014 zzy.cn
 *
 * 
 *******************************************************************************/
package com.ming.eureka;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import lombok.SneakyThrows;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * 文件操作工具
 * 
 * @author lll 2015年1月16日
 */
public class FileUtil {
	
	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            目录
	 * @return 返回目录创建后的路径
	 */
	public static File createFolder(String folderPath) {
		if (StringUtils.isBlank(folderPath)) {
			return null;
		}
		File myFilePath = null;
		try {
			myFilePath = new File(folderPath);
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			logger.error("{}", e);
		}
		return myFilePath;
	}

	/**
	 * 验证文件是否是图片
	 * 
	 * @param file
	 * @return
	 */
	@SuppressWarnings("finally")
	public static boolean isImage(File file) {
		boolean isImage = false;
		try {
			BufferedImage image = ImageIO.read(file);
			if (image != null) {
				isImage = true;
			}
		} catch (IOException ex) {
			logger.warn("{}", ex);
		} finally {
			return isImage;
		}
	}
	
	/**
	 * 获取图片
	 * @param is
	 * @return
	 */
	@SuppressWarnings("finally")
	public static BufferedImage getImage(InputStream is) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(is);
		} catch (IOException ex) {
			logger.warn("{}", ex);
		} finally {
			return image;
		} 
	}

	/**
	 * 多级目录创建
	 * 
	 * @param folderPath
	 *            准备要在本级目录下创建新目录的目录路径 例如 c:myf
	 * @param paths
	 *            无限级目录参数，各级目录以单数线区分 例如 a|b|c
	 */
	public static void createFolders(String folderPath, String paths) {
		try {
			String txt;
			StringTokenizer st = new StringTokenizer(paths, "|");
			while (st.hasMoreTokens()) {
				txt = st.nextToken().trim();
				if (folderPath.lastIndexOf(File.separator) != -1) {
					createFolder(folderPath + File.separator + txt);
				} else {
					createFolder(folderPath + txt);
				}
			}
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}

	/**
	 * byte数组转换成16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	/**
	 * 读文件
	 * @param file
	 * @param offset
	 * @param len
	 * @return
	 */
	@SneakyThrows
	public static byte[] readFile(File file, long offset, int len) {
		RandomAccessFile fr = new RandomAccessFile(file, "r");
		fr.seek(offset);
		byte[] data = new byte[len];
		fr.read(data);
		fr.close();
		return data;
	}

	public static String hashFile(InputStream inputStream) throws IOException {
		String hash = DigestUtils.md5Hex(inputStream);
		inputStream.reset();
		return hash;
	}
	
	
	public static String generateAndCreateHashPath(String dir, InputStream inputStream) throws IOException {
		return generateAndCreateHashPath(dir, hashFile(inputStream));
	}

	/**
	 * 目录结构生成 文件分散存储，防止某目录下文件过多
	 * 
	 * @param dir			目录
	 * @param fileName			原始文件名
	 * @return 文件路径：  "dir/hashpath/hashfileName"
	 */
	public static String generateAndCreateHashPath(String dir, String fileName) {
		return generateAndCreateHashPath(dir, fileName, "");
	}
	
	@SuppressWarnings("unchecked")
	public static String generateAndCreateHashPath(String dir, String hashfileName, String suffix) {
		
		int hashCode = hashfileName.hashCode();
		int dir1 = (hashCode >> 4) & 0xf;
		int dir2 = hashCode & 0xf;
		
		String newpath = null;
		if (dir.endsWith(File.separator)) {
			newpath = StringUtils.join(dir,
					dir1,File.separator,dir2,File.separator);
		} else {
			newpath = StringUtils.join(dir,
					File.separator, dir1, File.separator, dir2, File.separator);
		}

		File file = new File(newpath);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		return StringUtils.join(newpath, hashfileName, suffix);
	}
	
	public static void deleteFile(String path) {
		File file = new File(path);
		FileUtils.deleteQuietly(file);
	}
	
	/**
	 * 猜测contenttype
	 * @return
	 */
	public static String guessContentType(Path path) {
		try {
			return Files.probeContentType(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static final Map<String, String> contentTypeMap = Maps.newHashMap();
	
	
	/**
     * 压缩整个文件夹中的所有文件，生成指定名称的zip压缩包
     * @param filepath 文件所在目录
     * @param zippath 压缩后zip文件名称
     * @param dirFlag zip文件中第一层是否包含一级目录，true包含；false没有
     */
    public static File zipMultiFile(String filepath ,String zippath, boolean dirFlag) {
        try {
            File file = new File(filepath);// 要被压缩的文件夹
            File zipFile = new File(zippath);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            if(file.isDirectory()){
                File[] files = file.listFiles();
                if (files != null) {
                	for(File fileSec:files){
                		if(dirFlag){
                			recursionZip(zipOut, fileSec, file.getName() + File.separator);
                		}else{
                			recursionZip(zipOut, fileSec, "");
                		}
                	}
				}
            }
            zipOut.close();
            return zipFile;
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.error("压缩失败： {} to {}",filepath, zippath);
        }
        return null;
    }
     
    private static void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws Exception{
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File fileSec : files) {
					recursionZip(zipOut, fileSec, baseDir + file.getName()
							+ File.separator);
				}
			}
		} else {
			byte[] buf = new byte[1024];
			InputStream input = new FileInputStream(file);
			zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
			int len;
			while ((len = input.read(buf)) != -1) {
				zipOut.write(buf, 0, len);
			}
			input.close();
		}
    }

	/**
	 * base64字符串转化成图片,图片名称自动保存宽高
	 */
	public static String generateImageWithSizeAndSave(String base64ImgData, String uploadPath,String extensionFileName) {
		try {
			byte[] data = Base64.getDecoder().decode(base64ImgData);

			BufferedImage srcImage = ImageIO.read(new ByteArrayInputStream(data));
			int srcImageHeight = srcImage.getHeight();
			int srcImageWidth = srcImage.getWidth();

			String picName = FileUtil.generateRandomFilename()+"@"+srcImageWidth+"@"+srcImageHeight+"."+ extensionFileName;

			String imgFilePath = uploadPath + picName ;
			FileUtils.writeByteArrayToFile(new File(imgFilePath),data);
			return picName;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("保存图片失败");
		}
	}

	/**
	 * 生成时间戳格式的文件名称
	 */
	public static String generateRandomFilename() {
		int randCharCount = 4;
		StringBuilder fourRandom = new StringBuilder(String.valueOf(new Random().nextInt(10000)));
		int randLength = fourRandom.length();
		if (randLength < randCharCount) {
			for (int i = 1; i <= randCharCount - randLength; i++) {
				fourRandom.append("0");
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(CalendarUtil.getYear())
				.append(twoNumbers(CalendarUtil.getMonth()))
				.append(twoNumbers(CalendarUtil.getDayOfMonth()))
				.append(twoNumbers(CalendarUtil.getHour()))
				.append(twoNumbers(CalendarUtil.getMinute()))
				.append(twoNumbers(CalendarUtil.getSecond()))
				.append(fourRandom);
		return sb.toString();
	}
	private static String twoNumbers(int number) {
		String numberStr = number + "";
		if (numberStr.length() < 2) {
			numberStr = "0" + numberStr;
		}
		return numberStr;
	}
}

/**
 *
 */
package com.ming.eureka;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.ming.eureka.business.Query;
import com.ming.eureka.model.entity.file.AbstractFile;
import com.ming.eureka.model.entity.file.IFilePersistProcess;
import com.ming.eureka.model.entity.file.ImageProperties;
import com.ming.eureka.model.entity.file.SourceType;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * 文件上传service
 * @author lll 2015年7月14日
 */
@Service
@Slf4j
public class FileUploadService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    private ImageProperties imageProperties;

    @Value("${silk.v3.path}")
    private String silkV3Path;

    /**
     * 获取文件对象
     * @param sourceType
     * @param id
     * @return
     */
    public AbstractFile findFile(SourceType sourceType, long id) {
        return (AbstractFile) entityManager.find(sourceType.getFileClass(), id);
    }

    /**
     * 验证上传文件
     * @param mulFile
     * @param sourceType 类型
     * @return
     */
    public CommonResult validateFile(MultipartFile mulFile, SourceType sourceType) {
        /*
         *  1.检查文件是否为空
         *	3.检查文件类型
         */
        if (mulFile == null) {
            return new CommonResult(1, "file is empty");
        }

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = FileUtil.getImage(mulFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("非图片资源");
        }

        CommonResult result = CommonResult.succ();
        if (bufferedImage != null) {
            result.add("bufferedImage", bufferedImage);
        }
        return result;
    }

    /**
     * 获取文件
     * @param sourceType
     * @param id
     * @return
     */
    public AbstractFile getFileEntity(SourceType sourceType, long id) {
        AbstractFile fileEntity = this.findFile(sourceType, id);
        return fileEntity;
    }

    /**
     * 获取文件
     * @param file
     * @param spec
     * @param spec 指定图片规格
     * @return
     */
    public File getFile(AbstractFile file, String spec) {
        File fileT = null;
        if (file != null) {
            fileT = new File(file.getAbsolutePathInDisk(configInfo.getProFilePath(), spec));

            if (fileT.exists()) {
                return fileT;
            } else {
                logger.warn("文件不存在:file:{}", file.getFileUrl());
            }
        }
        return null;
    }

    /**
     * 获取临时的文件对象
     * @param uri
     * @param fileName
     * @param spec
     * @return
     * */
    public File getTempFile(String uri, String fileName, String spec, String filePath) {
        File file = null;
        if (filePath.endsWith(File.separator)) {
            filePath = StringUtils.join(filePath, uri);
        } else {
            filePath = StringUtils.join(filePath, File.separator, uri);
        }
        if (StringUtils.isNotBlank(spec)) {
            filePath = StringUtils.join(filePath, File.separator, spec, File.separator, fileName);
        } else {//原图文件
            filePath = StringUtils.join(filePath, File.separator, fileName);
        }
        file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            logger.info("临时文件不存在:file{}", filePath);
        }
        return null;
    }

    public File getOrCreateTempFile(String uri, String fileName)
            throws IOException {
        File file = null;
        String filePath = configInfo.getTemFilePath();
        // 获取绝对路径
        if (filePath.endsWith(File.separator)) {
            filePath = StringUtils.join(filePath, uri);
        } else {
            filePath = StringUtils.join(filePath, File.separator, uri);
        }

        FileUtil.createFolder(filePath);

        filePath = StringUtils.join(filePath, File.separator, fileName);
        logger.info("get file {}", filePath);
        file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 上传文件
     * @param sourceType    图片类型
     * @param customSpecs    自定义的规格
     * @param file
     * @param persistProcess 额外的持久化处理
     * @return
     * @throws IOException
     */
    @Transactional(readOnly = false)
    public AbstractFile uploadFile(SourceType sourceType, MultipartFile file, BufferedImage bufferedImage, String customSpecs,
                                   IFilePersistProcess persistProcess) throws IOException {
        Validate.notNull(persistProcess, "持久化处理不能为空");
        AbstractFile fileEntity = null;
        Map<String, Object> searchParams = persistProcess.searchParams();
        logger.info("开始上传文件...{}", searchParams);
        if (!CollectionUtils.isEmpty(searchParams)) {
            Query query = Query.forClass(sourceType.getFileClass(), entityManager);
            for (String key : searchParams.keySet()) {
                query.eq(key, searchParams.get(key));
            }
            List<?> resultList = entityManager.createQuery(query.newCriteriaQuery()).getResultList();
            fileEntity = CollectionUtils.isEmpty(resultList) ? null : (AbstractFile) resultList.iterator().next();

            logger.info("上传文件... 修改文件...FileEntity:{}", fileEntity);
        }
        //查询不到 新建记录
        if (fileEntity == null) {
            fileEntity = sourceType.createObject();
            logger.info("上传文件... 创建文件Entity...FileEntity:{}", fileEntity);
        }
        if (fileEntity == null) {
            logger.error("上传文件，初始化实体失败 searchParams:{}", searchParams);
            throw new ServiceException("上传文件，初始化实体失败");
        }
        List<String> imageSpecs = imageProperties.getSpecs(customSpecs, sourceType);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
        CommonResult result = this.saveFile(sourceType, inputStream, bufferedImage, imageSpecs, file.getContentType(), configInfo.getTemFilePath());
        if (bufferedImage != null) {
            fileEntity.setSpecs(StringUtils.join(imageSpecs, ','));
        }
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setSize(file.getSize());
        fileEntity.setUri((String) result.getValue("path"));
        if (StringUtils.isNotBlank(file.getContentType())) {
            logger.error("参数:{}", result.getValue("abPath").toString());
            if (file.getContentType().equals("audio/mp3") || isAud(new FileInputStream(result.getValue("abPath").toString()), true)) {
                fileEntity.setContentType("application/octet-stream");
            } else {
                fileEntity.setContentType(file.getContentType());
            }
        } else if (bufferedImage != null) {
            fileEntity.setContentType("image/jpeg");
        } else if (file.getOriginalFilename().endsWith("mp4")) {
            fileEntity.setContentType("video/mp4");
        }
        this.generateVideoFaceIfNecessary(fileEntity);
        persistProcess.persistProcess(fileEntity, sourceType);
        return fileEntity;
    }


    /**
     * 创建视频封面文件
     * @param file
     */
    public void generateVideoFaceIfNecessary(AbstractFile file) {
        if (StringUtils.isNotBlank(file.getContentType()) && file.getContentType().startsWith("video/")) {
            try {
                String desc = file.getAbsolutePathInDisk(configInfo.getTemFilePath(), AbstractFile.SPEC_VIDEO_FACE);
                FileUtil.createFolder(StringUtils.substringBeforeLast(desc, File.separator));
                VideoUtil.generateVideoFace(file.getAbsolutePathInDisk(configInfo.getTemFilePath(), null),
                        desc);
            } catch (Exception e) {
                CommonUtil.logException(logger, e, true);
            }
        }
    }

    /**
     * 生成缩略图存储图片
     * @param sourceType
     * @param inputStream
     * @param bufferedImage null 则重新获取
     * @param imageSpecs  null 则读取配置文件
     * @return
     */
    public CommonResult saveFile(SourceType sourceType, InputStream inputStream,
                                 BufferedImage bufferedImage, List<String> imageSpecs, String contentType, String dirPath) {
        Assert.notNull(inputStream, "图片资源不能为空");
        //资源类型目录 临时文件夹
        String sourceTypeDir = sourceType.pathInDir(dirPath);
        //生成绝对路径（并创建好相关目录）
        String descPath = null;
        try {
            descPath = FileUtil.generateAndCreateHashPath(sourceTypeDir, inputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
            logger.error("generate descPath error :" + e1.getMessage());
        }
        if (contentType.equals("audio/mp3")) {
            descPath = descPath + ".mp3";
        }
        // 转存文件
        assert descPath != null;
        File descFile = new File(descPath);
        try {
            if (bufferedImage == null) {
                bufferedImage = FileUtil.getImage(inputStream);
                inputStream.reset();
            }
            if (bufferedImage == null) {
                FileUtils.copyInputStreamToFile(inputStream, descFile);
                imageSpecs = Lists.newArrayList();
            } else {// 转存图片
                FileUtils.copyInputStreamToFile(inputStream, descFile);
                ImageUtil.dealAndSaveImage(descPath, descPath);
                if (imageSpecs == null) {
                    imageSpecs = imageProperties.getSpecs(null, sourceType);
                }
                //生成缩略图
                proccessImage(imageSpecs, bufferedImage, descPath);
                // 关闭流
                inputStream.close();
            }
            if (contentType.equals("audio/mp3") || isAud(new FileInputStream(descFile), true)) {
                resovleVoice(descFile, contentType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件处理错误：" + e.getMessage());
        }
        if (contentType.equals("audio/mp3")) {
            descPath = descPath.replace(".mp3", "");
        }
        String path = StringUtils.substringAfter(descPath, dirPath);
        return CommonResult.succ().withData("path", path).withData("imageSpecs", imageSpecs).withData("abPath", descPath);
    }


    /**
     * 删除文件
     * @deprecated 现在文件只存一份 慎用删除
     * @param sourceType    类型
     * @param fileId        文件id
     * @return
     */
    @Transactional(readOnly = false)
    public void deleteFile(SourceType sourceType, long fileId) {
        AbstractFile fileEntity = this.findFile(sourceType, fileId);

        if (fileEntity != null) {
            //删除文件
            fileEntity.removeAllFileInDisk(configInfo.getProFilePath());
            entityManager.remove(fileEntity);
        }
    }

    /**
     * 删除实体不删除文件
     * @param sourceType    类型
     * @param fileId        文件id
     * @return
     */
    @Transactional(readOnly = false)
    public void deleteEntityNoDelFile(SourceType sourceType, long fileId) {
        AbstractFile fileEntity = this.findFile(sourceType, fileId);
        if (fileEntity != null) {
            entityManager.remove(fileEntity);
        }
    }

    /**
     * 将临时文件移动到正式文件夹,并且保存到数据库
     * @return 保存到数据库的id的集合
     * */
    @Transactional(readOnly = false)
    public CommonResult moveTempFileToPro(SourceType sourceType, String tempPath) {
        return this.moveTempFileToPro(sourceType, tempPath, true);
    }

    /**
     * 将临时文件移动到正式文件夹,isAutoSave绝对是否自动保存到数据库
     * @param  sourceType 类型
     * @param tempPath  temp文件下的相对路径 如："sourType/hashPath1/hashPath2/文件名/文件类型/文件大小"  以，分割 可能是多个
     * 			"id,path|contentType|size" 可能是id 或 相对路径
     *
     * @return 返回文件对象集合
     * */
    @Transactional(readOnly = false)
    public CommonResult moveTempFileToPro(SourceType sourceType, String tempPath, boolean isAutoSave) {
        Assert.notNull(tempPath, "文件目录不能为空");
        Set<AbstractFile> mediaFiles = Sets.newHashSet();
        List<String> paths = Lists.newArrayList(StringUtils.split(tempPath.trim(), ","));
        String fileName, parentPath, absoluteTemPath, absoluteProPath;
        File fileT, fileP, subFileT, subFileP;
        for (String path : paths) { //逐个移动文件夹 并保存到数据库

            if (!path.contains("/")) {// 非临时文件
                mediaFiles.add(this.findFile(sourceType, Integer.parseInt(path)));
                continue;
            }

            List<String> cataLog = Lists.newArrayList(StringUtils.split(path, "|"));
            AbstractFile fileEntity = sourceType.createObject();
            absoluteTemPath = FilenameUtils.concat(configInfo.getTemFilePath(), cataLog.get(0));
            absoluteProPath = FilenameUtils.concat(configInfo.getProFilePath(), cataLog.get(0));
            try {

                fileName = FilenameUtils.getName(cataLog.get(0));
                parentPath = FilenameUtils.getPath(cataLog.get(0));

                fileT = new File(absoluteTemPath);
                fileP = new File(absoluteProPath);
                if (!fileP.exists()) {
                    Files.createParentDirs(fileP);
                    Files.move(fileT, fileP);
                }
                if (isAud(new FileInputStream(fileP), true)) {
                    fileT = new File(absoluteTemPath + ".mp3");
                    fileP = new File(absoluteProPath + ".mp3");
                    if (!fileP.exists()) {
                        Files.createParentDirs(fileP);
                        Files.move(fileT, fileP);
                    }
                }

                // 创建目录
                List<String> imageSpecs = new ArrayList<String>();
                if (cataLog.get(1).contains("video")) {
                    imageSpecs.add("vface");
                } else if (cataLog.get(1).contains("image")) {
                    imageSpecs = imageProperties.getSpecs(null, sourceType);
                }

                for (String spec : imageSpecs) {
                    subFileT = new File(FilenameUtils.concat(FilenameUtils.concat(fileT.getParent(), spec), fileName));
                    subFileP = new File(FilenameUtils.concat(FilenameUtils.concat(fileP.getParent(), spec), fileName));
                    if (!subFileP.exists()) {
                        Files.createParentDirs(subFileP);
                        Files.move(subFileT, subFileP);
                    }
                }

                fileEntity.setSpecs(StringUtils.join(imageSpecs, ','));
                fileEntity.setContentType(cataLog.get(1));

                fileEntity.setName(fileName);

                fileEntity.setSize(Long.valueOf(cataLog.get(2)));
                fileEntity.setUri(cataLog.get(0));
                //this.generateVideoFaceIfNecessary(fileEntity);
                if (isAutoSave) {
                    entityManager.persist(fileEntity);
                }
                mediaFiles.add(fileEntity);

            } catch (IOException e) {
                e.printStackTrace();
                logger.info("移动文件错误{}", absoluteProPath);
            }
        }
        CommonResult result = CommonResult.succ();
        result.add("mediaFiles", mediaFiles);
        return result;
    }


    /**
     *
     * @param imageSpecs
     * @param img
     * @param srcPath
     *            图片规格裁切说明：
     *
     *            1x320x320 --> 等比压缩图片，可能不符合规格，图片不变行  --压缩
     *            2x320x320 --> 非等比压缩图片，完全符合规格，图片可能变形  --压缩
     *            3x540x540x320x320 --> 先压缩（540x540）后裁剪 (320x320) 等比操作   --先压缩后居中裁切
     *            4x540x540x320x320 --> 先裁切（540x540） 后压缩（320x320）等比操作 --先裁切后压缩
     *            5x100x100x400x400 --> 根据图片坐标裁切图片 起点坐标：（100x100） 终点坐标 ：（400x400）--裁切
     *            6x100x100x540x540 --> 根据图片坐标和指定宽高裁切图片  起点坐标：（100x100） 裁切宽高（540x540）--裁切
     *            7x0x0x540x540x300x300 --> 先根据坐标裁切,再压缩,再裁切  起点坐标:（0x0）裁切宽高：（300x300）压缩宽高：（540x540）--先裁切后压缩
     *            8x0x0x500x500x200x200 --> 先根据坐标裁切,再压缩 起点坐标：（100x100） 终点坐标 ：（500x500）压缩宽高（200x200） --先裁切后压缩
     *            9x300x300 --> 头像简单处理
     *            12x? --> 宽度定为指定的大小,高度等比例缩放
     *            13x? --> 高度定为指定的大小,宽度等比例缩放
     *
     */
    private void proccessImage(Collection<String> imageSpecs,
                               BufferedImage img, String srcPath) throws Exception {

        String descPath = null;

        for (String spec : imageSpecs) {
            String descDir = StringUtils.substringBeforeLast(srcPath, File.separator);
            String fileName = StringUtils.substringAfterLast(srcPath, File.separator);

            FileUtil.createFolder(StringUtils.join(descDir, File.separator, spec));
            descPath = StringUtils.join(descDir,
                    File.separator, spec,
                    File.separator, fileName);

            String[] size = spec.split("x");
            Integer key = Integer.parseInt(size[0]);

            if (key == 1) {

                ImageUtil.resizeImageKeepScale(srcPath, Integer.valueOf(size[1]), Integer.parseInt(size[2]),
                        img, descPath);

            } else if (key == 2) {

                ImageUtil.resizeImage(srcPath, Integer.valueOf(size[1]),
                        Integer.parseInt(size[2]), img, descPath);
            } else if (key == 3) {

                ImageUtil.pressAndCutImage(srcPath,
                        Integer.valueOf(size[1]), Integer.parseInt(size[2]),
                        Integer.valueOf(size[3]), Integer.parseInt(size[4]),
                        img, descPath);
            } else if (key == 4) {

                ImageUtil.cutAndPressImage(srcPath,
                        Integer.valueOf(size[1]), Integer.parseInt(size[2]),
                        Integer.valueOf(size[3]), Integer.parseInt(size[4]),
                        img, descPath);
            } else if (key == 5) {

                ImageUtil.cutImageByPoint(srcPath, img, descPath,
                        Integer.parseInt(size[1]), Integer.parseInt(size[2]),
                        Integer.parseInt(size[3]), Integer.parseInt(size[4]));
            } else if (key == 6) {

                ImageUtil.cutImageByPointAndWH(srcPath, img, descPath,
                        Integer.parseInt(size[1]), Integer.parseInt(size[2]),
                        Integer.parseInt(size[3]), Integer.parseInt(size[4]));
            } else if (key == 7) {

                ImageUtil.cutImageByPointWHAndPress(srcPath, img, descPath, Integer.parseInt(size[1]),
                        Integer.parseInt(size[2]), Integer.parseInt(size[3]),
                        Integer.parseInt(size[4]), Integer.parseInt(size[5]),
                        Integer.parseInt(size[6]));
            } else if (key == 8) {

                ImageUtil.cutImageByPointAndPress(srcPath, img, descPath,
                        Integer.parseInt(size[1]), Integer.parseInt(size[2]),
                        Integer.parseInt(size[3]), Integer.parseInt(size[4]),
                        Integer.parseInt(size[5]), Integer.parseInt(size[6]));

            } else if (key == 9) {

                ImageUtil.cropImageCenter(srcPath, img, descPath,
                        Integer.parseInt(size[1]), Integer.parseInt(size[2]));
            } else if (key == 12) {
                ImageUtil.resizeImageFixWidth(srcPath, Integer.parseInt(size[1]), img, descPath);
            } else if (key == 13) {
                ImageUtil.resizeImageFixHeight(srcPath, Integer.parseInt(size[1]), img, descPath);
            }
            // 后续扩展
            // TODO
            // case 9:
            //
            // break;
            // case 0:
            //
            // break;
        }
    }

    public int resovleVoice(File file, String extensions) {
        if (file.exists()) {
            if (extensions.equals("audio/mp3")) {
                int status = ShellUtil.callScript("converter_encoder.sh", file.getAbsolutePath() + " " + file.getParentFile().getAbsolutePath() + " " + file.getName().split("\\.")[0], silkV3Path);
                if (status == 0) {
                    new File(file.getAbsolutePath().replace(".mp3", ".aud")).renameTo(new File(file.getAbsolutePath().replace(".mp3", "")));
                    return 0;
                }
                return 1;
            } else {
                return ShellUtil.callScript("converter.sh", file.getAbsolutePath() + " mp3", silkV3Path);
            }
        }
        return 1;
    }

    /**
     * 判断是不是aud格式
     * @param stream
     * @return
     * @throws IOException
     */
    public boolean isAud(InputStream stream, boolean needClose) {
        try {
            int pos = 1;//从第几个字节开始读
            int len = 9;//读几个字节
            stream.skip(pos); //跳过之前的字节数
            byte[] b = new byte[len];
            stream.read(b);
            if (needClose) {
                stream.close();
            }
            logger.warn("e{}", new String(b));
            return new String(b).equals("#!SILK_V3");
        } catch (IOException e) {
            logger.warn("e{}", "文件不正常打开或关闭");
            return false;
        }
    }
}

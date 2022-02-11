/**
 *
 */
package com.ming.eureka.controller;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.ming.eureka.CommonResult;
import com.ming.eureka.FileUploadService;
import com.ming.eureka.business.FileView;
import com.ming.eureka.model.entity.file.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.ming.eureka.model.entity.file.ImagesDto.ImageDto;

/**
 * 文件,图片管理控制器
 */
@Controller
@RequestMapping(value = "/api/files")
@Slf4j
@CrossOrigin
public class FileController {

    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    /** 文件,图片上传业务层 */
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private ConfigInfo configInfo;

    @Value("${files.pro.path}")
    private String uploadPath;

    /**
     * 获取图片
     * @param fileId
     * @param sourceType 图片类型
     * @param spec 缩略图规格
     *
     *
     *
     * 				9x120x120 缩略图
     * 				face 视频封面
     * @return
     */
    @RequestMapping(value = "{sourceType}/{id}/{spec}")
    public String get(@PathVariable("id") long fileId,
                      @PathVariable SourceType sourceType, @PathVariable() String spec,
                      HttpServletResponse response, Model model) {
        // 原图
        if (spec.equals(AbstractFile.SPEC_ORIGNAL)) {
            spec = null;
        }

        AbstractFile fileModel = fileUploadService.getFileEntity(sourceType, fileId);
        File file = fileUploadService.getFile(fileModel, spec);
        if (file == null) {
            file = fileUploadService.getFile(fileModel, null);
        }
        //判断文件是否存在
        if (file != null) {
            //1年
            response.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);
            response.setHeader("Cache-Control", "public,max-age=31536000");
            if (AbstractFile.SPEC_VIDEO_FACE.equals(spec)) {
                model.addAttribute("contentType", "image/png");
            } else if (fileModel.getContentType() != null) {
                model.addAttribute("contentType", fileModel.getContentType());
            }
            response.setContentLength((int) FileUtils.sizeOf(file));
            model.addAttribute("file", file);
        }
        return FileView.VIEW_NAME;
    }

    /**
     * 获取图片
     * @param sourceType 图片类型
     * @param dir1 第一层目录
     * @param dir2 第二层目录
     * @param fileName 文件名
     * @param spec 缩略图规格
     * 				0为原图
     * 				9x120x120 缩略图
     * 				face 视频封面
     * @return
     */
    @RequestMapping(value = "{sourceType}/{dir1}/{dir2}/{fileName}/{spec}")
    public String getTempFile(@PathVariable SourceType sourceType, @PathVariable String dir1, @PathVariable String dir2, @PathVariable String fileName,
                              @PathVariable String spec, @RequestParam(value = "content", required = false, defaultValue = "image/jpeg") String content, Model model, HttpServletResponse response
    ) throws FileNotFoundException {
        if (spec.equals(AbstractFile.SPEC_ORIGNAL)) {
            spec = null;
        }
        String uri = sourceType + "/" + dir1 + "/" + dir2;
        File file = fileUploadService.getTempFile(uri, fileName, spec, configInfo.getTemFilePath());
        //判断文件是否存在
        if (file != null) {
            //1年
            response.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);
            response.setHeader("Cache-Control", "public,max-age=31536000");
            model.addAttribute("contentType", content);
            response.setContentLength((int) FileUtils.sizeOf(file));
            model.addAttribute("file", file);
        }
        return FileView.VIEW_NAME;
    }


    /**
     * 上传图片
     * @param sourceType 图片类型
     * @param customSpecs 缩略图额外规格，逗号分隔
     * @return
     */
    @RequestMapping(value = "/upload")
    @ResponseBody
    public CommonResult upload(MultipartHttpServletRequest request,
                               @RequestParam SourceType sourceType, String customSpecs) {
        List<AbstractFile> files = Lists.newArrayList();
        Iterator<String> itr = request.getFileNames();
        MultipartFile file;
        while (itr.hasNext()) {
            //获取请求中的文件数据对象
            file = request.getFile(itr.next());
            // 基本信息验证
            CommonResult result = fileUploadService.validateFile(file, sourceType);
            if (result.isFail()) {
                return result;
            }
            // 业务处理 上传至临时的文件夹
            try {
                AbstractFile fileEntity = fileUploadService.uploadFile(sourceType, file, (BufferedImage) result.getAnyData(), customSpecs, new IFilePersistProcess() {
                    @Override
                    public Map<String, Object> searchParams() {
                        return null;
                    }

                    @Override
                    public void persistProcess(AbstractFile file, SourceType sourceType) {
                    }
                });
                files.add(fileEntity);
            } catch (IOException e) {
                e.printStackTrace();
                return CommonResult.commError(e.getMessage());
            }
        }
//		System.out.print("==========="+files.get(0).getFileUrl());

        ImagesDto imagesDto = new ImagesDto();
        //返回文件结果集
        Collection<ImageDto> images = Collections2.transform(files, file1 -> new ImageDto(file1));
        imagesDto.setFiles(images);

        Iterator it = imagesDto.getFiles().iterator();
        while (it.hasNext()) {
            ImageDto ImageDto = (ImagesDto.ImageDto) it.next();
            System.out.print("===========" + ImageDto.getUrl());
        }


        return CommonResult.succ().withResult(imagesDto);
    }

    /**
     * 删除图片
     * @param fileId 文件ID
     * @param sourceType 图片类型
     * @return
     */
    @RequestMapping(value = "/delete/{sourceType}/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    CommonResult delete(@PathVariable("id") long fileId, @PathVariable SourceType sourceType) {
        // 业务处理
        fileUploadService.deleteFile(sourceType, fileId);
        return CommonResult.succ();
    }

}

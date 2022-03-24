/**
 *
 */
package com.ming.eureka.restcontroller;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.ming.eureka.CommonResult;
import com.ming.eureka.FileUploadService;
import com.ming.eureka.business.FileView;
import com.ming.eureka.model.entity.file.*;
import com.ming.eureka.model.entity.file.ImagesDto.ImageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文件,图片管理控制器
 */
@RestController
@RequestMapping(value = "/api/files")
@Slf4j
@Api(tags="文件相关接口")
//允许跨域
//@CrossOrigin
public class FileController {

    /** 文件,图片上传业务层 */
    @Autowired
    private FileUploadService fileUploadService;
    /**
     * 配置读取实体
     */
    @Autowired
    private ConfigInfo configInfo;

    /**
     * 获取文件（图片或者视频或者音频）
     * @param fileId 文件id
     * @param sourceType 文件类型
     * @param spec 规格
     * @param response 返回对象
     * @param model model
     * @return 文件视图
     */
    @RequestMapping(value = "{sourceType}/{id}/{spec}",method = RequestMethod.GET)
    @ApiOperation(value = "获取正式图片文件")
    public String get(@PathVariable("id") long fileId,
                      @PathVariable SourceType sourceType,
                      @PathVariable() String spec,
                      HttpServletResponse response, Model model) {
        // 判断是不是原图
        if (spec.equals(AbstractFile.SPEC_ORIGNAL)) {
            spec = null;
        }
        //获取图片数据库存储信息
        AbstractFile fileModel = fileUploadService.getFileEntity(sourceType, fileId);
        File file = fileUploadService.getFile(fileModel, spec);
        if (file == null) {
            //如果没传规格过来，获取原图
            file = fileUploadService.getFile(fileModel, null);
        }
        //判断文件是否存在
        if (file != null) {
            //1年
            response.setDateHeader("Expires", System.currentTimeMillis() + 31536000000L);
            response.setHeader("Cache-Control", "public,max-age=31536000");
            if (AbstractFile.SPEC_VIDEO_FACE.equals(spec)) {
                //封面传图
                model.addAttribute("contentType", "image/png");
            } else if (fileModel.getContentType() != null) {
                //原类型
                model.addAttribute("contentType", fileModel.getContentType());
            }
            response.setContentLength((int) FileUtils.sizeOf(file));
            model.addAttribute("file", file);
        }
        return FileView.VIEW_NAME;
    }

    /**
     * 获取临时文件
     * @param sourceType 文件类型
     * @param dir1 目录1
     * @param dir2 目录2
     * @param fileName 文件名
     * @param spec 规格
     * @param content 内容
     * @param model model
     * @param response 返回对象
     * @return 视图
     * @throws FileNotFoundException
     */
    @RequestMapping(value = "{sourceType}/{dir1}/{dir2}/{fileName}/{spec}",method = RequestMethod.GET)
    @ApiOperation(value = "获取临时文件图片")
    public String getTempFile(@PathVariable SourceType sourceType,
                              @PathVariable String dir1,
                              @PathVariable String dir2,
                              @PathVariable String fileName,
                              @PathVariable String spec,
                              @RequestParam(value = "content", required = false, defaultValue = "image/jpeg") String content,
                              Model model, HttpServletResponse response
    ){
        if (spec.equals(AbstractFile.SPEC_ORIGNAL)) {
            //0是原规格
            spec = null;
        }
        //路径(类型+哈希值1+哈希值2)
        String uri = sourceType + "/" + dir1 + "/" + dir2;
        //获取文件所在的本地路径（fileName绝对是hash值的文件名）
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
     * @return 返回结果
     */
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ApiOperation(value = "文件上传")
    public CommonResult upload(MultipartHttpServletRequest request,
                               @RequestParam SourceType sourceType,
                               String customSpecs) {
        List<AbstractFile> files = Lists.newArrayList();
        Iterator<String> itr = request.getFileNames();
        //多文件类型
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
     * 单上传图片（仅仅为了api测试）
     * @param sourceType 图片类型
     * @param customSpecs 缩略图额外规格，逗号分隔
     * @return 返回结果
     */
    @RequestMapping(value = "/sinleUpload/",method = RequestMethod.POST,headers = "content-type=multipart/form-data")
    @ApiOperation(value = "文件上传1")
    public CommonResult singleUpload(@ApiParam(value = "医院图片", required = true) MultipartFile file,
                               @RequestParam SourceType sourceType,
                               String customSpecs) {
        List<AbstractFile> files = Lists.newArrayList();
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
    @ApiOperation(value = "通过类型和id删除文件")
    @RequestMapping(value = "/delete/{sourceType}/{id}", method = RequestMethod.DELETE)
    public CommonResult delete(@PathVariable("id") long fileId,
                               @PathVariable SourceType sourceType) {
        // 业务处理
        fileUploadService.deleteFile(sourceType, fileId);
        return CommonResult.succ();
    }

}

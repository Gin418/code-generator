package com.code.web.controller;

import cn.hutool.core.io.FileUtil;
import com.code.web.annotation.AuthCheck;
import com.code.web.common.BaseResponse;
import com.code.web.common.ErrorCode;
import com.code.web.common.ResultUtils;
import com.code.web.constant.UserConstant;
import com.code.web.exception.BusinessException;
import com.code.web.manager.CosManager;
import com.code.web.model.dto.file.UploadFileRequest;
import com.code.web.model.entity.User;
import com.code.web.model.enums.FileUploadBizEnum;
import com.code.web.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    /**
     * @param multipartFile
     * @return com.code.web.common.BaseResponse<java.lang.String>
     * @throws
     * @title testUploadFile
     * @date 2024/12/12
     * @description 测试文件上传
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestParam("file") MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        //文件写入到本地
        String projectPath = System.getProperty("user.dir");
        String filepath = projectPath + File.separator + ".temp/test/" + filename;
        if (FileUtil.exist(filepath)) {
            FileUtil.touch(filepath);
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filepath);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
            log.info("文件写入本地成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(/*FileConstant.COS_HOST +*/ filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * @param filepath
     * @param response
     * @throws
     * @title testDownloadFile
     * @date 2024/12/12
     * @description 测试文件下载
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws RuntimeException, IOException {
        System.out.println("filePath = " + filepath);
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filePath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            // 用完流之后一定要调用 close()
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);

        // 如果是模板文件则不上传到对象存储
        if (FileUploadBizEnum.GENERATOR_MAKE_TEMPLATE.getValue().equals(biz)) {
            //文件写入到本地
            String projectPath = System.getProperty("user.dir");
            String tempDirPath = String.format("/.temp/template/%s", filename);
            String localFilePath = projectPath + tempDirPath;
            // 新建文件
            if (!FileUtil.exist(localFilePath)) {
                FileUtil.touch(localFilePath);
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(localFilePath);
                fileOutputStream.write(multipartFile.getBytes());
                fileOutputStream.close();
                log.info("文件写入成功,本地位置：{}", localFilePath);
                return ResultUtils.success(tempDirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            log.info("文件上传成功,远程位置：{}", filepath);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}

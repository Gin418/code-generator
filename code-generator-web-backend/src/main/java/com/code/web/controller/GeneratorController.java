package com.code.web.controller;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.code.maker.generator.main.GenerateTemplate;
import com.code.maker.generator.main.ZipGenerator;
import com.code.maker.meta.Meta;
import com.code.maker.meta.MetaValidator;
import com.code.web.annotation.AuthCheck;
import com.code.web.common.BaseResponse;
import com.code.web.common.DeleteRequest;
import com.code.web.common.ErrorCode;
import com.code.web.common.ResultUtils;
import com.code.web.constant.UserConstant;
import com.code.web.exception.BusinessException;
import com.code.web.exception.ThrowUtils;
import com.code.web.manager.CacheManager;
import com.code.web.manager.CosManager;
import com.code.web.model.dto.generator.*;
import com.code.web.model.entity.Generator;
import com.code.web.model.entity.User;
import com.code.web.model.vo.GeneratorVO;
import com.code.web.service.GeneratorService;
import com.code.web.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 代码生成器接口
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private CacheManager cacheManager;

    // region 增删改查

    /**
     * 创建
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info(String.valueOf(generatorAddRequest));
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        List<String> tags = generatorAddRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorAddRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        log.info(String.valueOf(generator));
        // 参数校验
        generatorService.validGenerator(generator, true);
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        generator.setStatus(0);
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        List<String> tags = generatorUpdateRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorUpdateRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorUpdateRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);

        // 删除缓存
        if (!oldGenerator.getDistPath().equals(generator.getDistPath())) {
            String zipFilePath = getCacheFilePath(id, oldGenerator.getDistPath());
            FileUtil.del(zipFilePath);
        }

        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                 HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        stopWatch.stop();
        System.out.println("查询生成器耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        stopWatch = new StopWatch();
        stopWatch.start();
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);
        stopWatch.stop();
        System.out.println("查询关联数据耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        return ResultUtils.success(generatorVOPage);
    }

    /**
     * 快速分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo/fast")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageFast(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                     HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 多级缓存
        String cacheKey = getPageCacheKey(generatorQueryRequest);
        Object cacheValue = cacheManager.get(cacheKey);
        if (cacheValue != null) {
            log.info("命中多级缓存: " + cacheKey);
            //Page<GeneratorVO> generatorVOPage = JSONUtil.toBean(cacheValue, new TypeReference<Page<GeneratorVO>>() {
            //}, false);
            return ResultUtils.success((Page<GeneratorVO>) cacheValue);
        }

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Generator> queryWrapper = generatorService.getQueryWrapper(generatorQueryRequest);
        queryWrapper.select("id", "name", "description", "tags", "picture", "status", "userId", "createTime", "updateTime");
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), queryWrapper);
        generatorPage.getRecords().forEach(generator -> {
            generator.setFileConfig(null);
            generator.setModelConfig(null);
        });
        Page<GeneratorVO> generatorVOPage = generatorService.getGeneratorVOPage(generatorPage, request);

        // 写入多级缓存
        log.info("写入多级缓存: " + cacheKey);
        cacheManager.put(cacheKey, generatorVOPage);
        return ResultUtils.success(generatorVOPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                   HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        List<String> tags = generatorEditRequest.getTags();
        generator.setTags(JSONUtil.toJsonStr(tags));
        Meta.FileConfig fileConfig = generatorEditRequest.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorEditRequest.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * @param id       文件 id
     * @param request
     * @param response
     * @throws
     * @title downloadGeneratorById
     * @date 2024/12/15
     * @description 根据 id 下载
     */
    @GetMapping("/download")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String filepath = generator.getDistPath();
        if (StrUtil.isBlank(filepath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 追踪事件
        log.info("用户 {} 下载了 {}", loginUser, filepath);

        // 将数据存储类型改为标准存储
        cosManager.copyObject(filepath);

        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filepath);

        String zipFilePath = getCacheFilePath(id, filepath);
        if (FileUtil.exist(zipFilePath)) {
            // 写入响应
            Files.copy(Paths.get(zipFilePath), response.getOutputStream());
            return;
        }

        COSObjectInputStream cosObjectInput = null;
        try {
            // 开始计时
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();

            // 将 InputStream 写入到 HttpServletResponse 的输出流中
            try (OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = cosObjectInput.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                // 处理异常
                e.printStackTrace();
            }

            // 处理下载到的流
//            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            // 停止计时
            stopWatch.stop();
            System.out.println("下载产物包耗时: " + stopWatch.getTotalTimeMillis() + "ms");


            // 写入响应
//            response.getOutputStream().write(bytes);
//            response.getOutputStream().flush();
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
     * @param generatorUseRequest
     * @param request
     * @param response
     * @throws
     * @title useGenerator
     * @date 2024/12/17
     * @description 使用代码生成器
     */
    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 获取用户输入的请求参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        log.info("用户{}，使用了生成器 id = {}", loginUser, id);

        // 2. 从对象存储上下载生成器的产物包
        // 2.1. 获取产物包的存储路径
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 将数据存储类型改为标准存储
        cosManager.copyObject(distPath);

        // 2.2. 定义一个独立的工作空间，存放临时数据
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/use/%s", projectPath, id);


        StopWatch stopWatch;
        // 查询缓存
        String zipFilePath = getCacheFilePath(id, distPath);
        if (!FileUtil.exist(zipFilePath)) {
            zipFilePath = tempDirPath + "/dist.zip";
            // 新建文件
            if (!FileUtil.exist(zipFilePath)) {
                FileUtil.touch(zipFilePath);
            }
            // 2.3. 下载生成器的产物包
            // 开始计时
            stopWatch = new StopWatch();
            stopWatch.start();
            try {
                cosManager.download(distPath, zipFilePath);
            } catch (InterruptedException e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
            }
            // 停止计时
            stopWatch.stop();
            System.out.println("下载产物包耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        }

        // 3. 解压压缩包，得到脚本文件
        stopWatch = new StopWatch();
        stopWatch.start();
        File unzipDistDir = ZipUtil.unzip(zipFilePath, tempDirPath);
        stopWatch.stop();
        System.out.println("解压产物包耗时: " + stopWatch.getTotalTimeMillis() + "ms");

        // 4. 将用户输入的参数写到 JSON 文件中
        stopWatch = new StopWatch();
        stopWatch.start();
        String dataModelFilePath = tempDirPath + "/dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);
        stopWatch.stop();
        System.out.println("写数据文件耗时: " + stopWatch.getTotalTimeMillis() + "ms");

        // 5. 执行脚本
        // 5.1. 获取脚本文件所在路径
        // windows 系统
        /*File scriptFile = FileUtil.loopFiles(unzipDistDir, 2, null).stream()
                .filter(file -> file.isFile() && "generator.bat".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);*/

        // linux / mac 系统
        File scriptFile = FileUtil.loopFiles(unzipDistDir, 2, null).stream()
                .filter(file -> file.isFile() && "generator".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        // 5.2. 添加可执行权限
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
        } catch (Exception e) {
        }

        // 5.3. 构造命令
        File scriptDir = scriptFile.getParentFile();
        // windows 系统未知问题！！，访问命令行脚本必须要使用绝对路径
//         String scriptAbsolutePath = scriptFile.getAbsolutePath().replace("\\", "/");
//         String[] commands = new String[]{scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};

        // Linux / mac 系统，要用 ./generator
        String scriptAbsolutePath = scriptFile.getAbsolutePath();
        String[] commands = new String[]{scriptAbsolutePath, "json-generate", "--file=" + dataModelFilePath};

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(unzipDistDir);

        try {
            stopWatch = new StopWatch();
            stopWatch.start();
            Process process = processBuilder.start();

            // 读取命令的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("命令执行结果，退出码: " + exitCode);
            stopWatch.stop();
            System.out.println("执行脚本耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行生成器脚本失败");
        }

        // 使用数加 1
        generator.setUseNum(generator.getUseNum() + 1);
        generatorService.updateById(generator);

        // 6. 压缩得到的生成器，返回给前端
        stopWatch = new StopWatch();
        stopWatch.start();
        String generatedPath = scriptDir.getAbsolutePath() + "/generated";
        String resultPath = tempDirPath + "/result.zip";
        File resultFile = ZipUtil.zip(generatedPath, resultPath);
        stopWatch.stop();
        System.out.println("压缩生成器耗时: " + stopWatch.getTotalTimeMillis() + "ms");

        // 下载文件
        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        // 写入响应
        Files.copy(resultFile.toPath(), response.getOutputStream());

        // 7. 异步执行删除临时文件
        CompletableFuture.runAsync(() -> FileUtil.del(tempDirPath));
    }

    /**
     * @param generatorMakeRequest
     * @param request
     * @param response
     * @throws
     * @title makeGenerator
     * @date 2024/12/17
     * @description 制作代码生成器
     */
    @PostMapping("/make")
    public void makeGenerator(
            @RequestBody GeneratorMakeRequest generatorMakeRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        // 1. 获取用户输入的请求参数
        Meta meta = generatorMakeRequest.getMeta();
        String zipFilePath = generatorMakeRequest.getZipFilePath();

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("用户{}，正在制作生成器", loginUser);

        // 2. 创建独立的工作空间，下载压缩包到本地
        String projectPath = System.getProperty("user.dir");
        String localZipFilePath = projectPath + zipFilePath;
        System.out.println("模板文件本地路径: " + localZipFilePath);

        StopWatch stopWatch;

        String id = IdUtil.getSnowflakeNextId() + RandomUtil.randomString(6);
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);

        /*String localZipFilePath = tempDirPath + "/project.zip";
        // 新建文件
        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }

        // 下载文件
        try {
            stopWatch = new StopWatch();
            stopWatch.start();
            cosManager.download(zipFilePath, localZipFilePath);
            stopWatch.stop();
            System.out.println("下载模板文件耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "压缩包下载失败");
        }*/

        // 3. 解压，得到项目模板文件
        File unzipDistDir = ZipUtil.unzip(localZipFilePath, tempDirPath + "/project");

        // 4. 构造 meta 对象和生成器输出路径
        String sourceRootPath = unzipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        // 校验和处理默认值
        stopWatch = new StopWatch();
        stopWatch.start();
        MetaValidator.doValidAndFill(meta);
        stopWatch.stop();
        System.out.println("校验 meta 信息耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        String outputPath = tempDirPath + File.separator + "generated" + File.separator + meta.getName();

        // 5. 调用 maker 方法制作生成器
        GenerateTemplate generate = new ZipGenerator();
        try {
            stopWatch = new StopWatch();
            stopWatch.start();
            generate.doGenerate(meta, outputPath);
            stopWatch.stop();
            System.out.println("制作生成器耗时: " + stopWatch.getTotalTimeMillis() + "ms");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器制作失败");
        }

        // 6. 压缩生成器，返回给前端下载
        String zipFileName = "dist.zip";
        String distZipFilePath = outputPath + File.separator + zipFileName;

        // 下载文件
        // 设置响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        // 写入响应
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());

        // 7. 异步执行删除临时文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
            FileUtil.del(localZipFilePath);
        });
    }

    /**
     * @param generatorCacheRequest
     * @param request
     * @param response
     * @throws
     * @title cacheGenerator
     * @date 2024/12/22
     * @description 缓存代码生成器
     */
    @PostMapping("/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (generatorCacheRequest == null || generatorCacheRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = generatorCacheRequest.getId();
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        // 缓存空间
        String zipFilePath = getCacheFilePath(id, distPath);

        // 新建文件
        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }

        try {
            cosManager.download(distPath, zipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
        }
    }

    /**
     * @param id
     * @param distPath
     * @return java.lang.String
     * @throws
     * @title getCacheFilePath
     * @date 2024/12/22
     * @description 获取缓存文件路径
     */
    public String getCacheFilePath(long id, String distPath) {
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = String.format("%s/.temp/cache/%s", projectPath, id);
        return tempDirPath + File.separator + distPath;
    }

    /**
     * @param generatorQueryRequest
     * @return java.lang.String
     * @throws
     * @title getPageCacheKey
     * @date 2024/12/23
     * @description 获取分页缓存的 key
     */
    private static String getPageCacheKey(GeneratorQueryRequest generatorQueryRequest) {
        String jsonStr = JSONUtil.toJsonStr(generatorQueryRequest);
        // 请求参数编码
        String base64 = Base64Encoder.encode(jsonStr);
        String key = "generator:page:" + base64;
        return key;
    }
}

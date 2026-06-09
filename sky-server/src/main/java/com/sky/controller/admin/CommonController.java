package com.sky.controller.admin;

import com.sky.annotation.RateLimit;
import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/common")
@Tag(name="通用接口")
public class CommonController {

    /** 允许上传的文件扩展名白名单 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".gif");

    /** 最大文件大小：2MB */
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024L;

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @Operation(summary ="文件上传")
    @RateLimit(permitsPerSecond = 3, message = "文件上传过于频繁，请稍后再试")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}", file.getOriginalFilename());

        // 校验文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BaseException(MessageConstant.UPLOAD_FILE_EMPTY);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BaseException(MessageConstant.UPLOAD_FILE_EMPTY);
        }

        // 校验文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BaseException(MessageConstant.UPLOAD_FILE_TYPE_NOT_ALLOWED);
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BaseException(MessageConstant.UPLOAD_FILE_SIZE_EXCEEDED);
        }

        try {
            String objectName = UUID.randomUUID().toString() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BaseException(MessageConstant.UPLOAD_FAILED);
        }
    }
}



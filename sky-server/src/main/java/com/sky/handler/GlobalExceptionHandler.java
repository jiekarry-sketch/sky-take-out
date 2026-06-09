package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获数据库唯一约束冲突异常
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.error("数据库约束异常：{}", message);
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            return Result.error(username + MessageConstant.ALREADY_EXIST);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 捕获参数校验异常（@Valid校验失败）
     */
    @ExceptionHandler
    public Result exceptionHandler(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.error("参数校验异常：{}", message);
        return Result.error(message);
    }

    /**
     * 捕获资源不存在异常
     */
    @ExceptionHandler
    public Result exceptionHandler(NoResourceFoundException ex){
        log.error("资源不存在：{}", ex.getMessage());
        return Result.error("请求的资源不存在");
    }

    /**
     * 捕获空指针异常（兜底，防止500泄露给前端）
     */
    @ExceptionHandler
    public Result exceptionHandler(NullPointerException ex){
        log.error("空指针异常：", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 兜底：捕获所有未处理的异常
     * 注意：此方法必须放在最后，Spring按声明顺序匹配异常处理器
     */
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception ex){
        // 检查是否是被AOP包装的业务异常
        Throwable cause = ex.getCause();
        if (cause instanceof BaseException) {
            log.error("业务异常（AOP包装）：{}", cause.getMessage());
            return Result.error(cause.getMessage());
        }
        log.error("系统异常：", ex);
        return Result.error("系统繁忙，请稍后再试");
    }

}

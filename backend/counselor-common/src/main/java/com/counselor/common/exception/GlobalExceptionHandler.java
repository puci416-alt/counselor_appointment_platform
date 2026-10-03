package com.counselor.common.exception;

import com.counselor.common.result.Result;
import com.counselor.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常（@Valid 触发）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数错误";
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数错误";
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 唯一索引冲突
     */
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(org.springframework.dao.DuplicateKeyException e) {
        log.warn("唯一索引冲突: {}", e.getMessage());
        // 根据索引名判断是哪种冲突
        String msg = e.getMessage();
        if (msg != null && msg.contains("uk_schedule_id")) {
            return Result.error(ResultCode.SCHEDULE_ALREADY_BOOKED);
        }
        if (msg != null && msg.contains("uk_order_no")) {
            return Result.error("订单编号重复，请重试");
        }
        return Result.error("数据已存在，请勿重复提交");
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.ERROR.getCode(), "系统繁忙，请稍后再试");
    }
}
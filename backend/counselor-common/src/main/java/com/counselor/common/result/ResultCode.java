package com.counselor.common.result;

import lombok.Getter;

/**
 * 统一返回码
 */
@Getter
public enum ResultCode {

    // ========== 通用 ==========
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或 Token 已失效"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // ========== 用户相关 1000+ ==========
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_ALREADY_EXISTS(1003, "用户已存在"),

    // ========== 咨询师相关 2000+ ==========
    COUNSELOR_NOT_FOUND(2001, "咨询师不存在"),
    COUNSELOR_OFFLINE(2002, "咨询师已下架"),

    // ========== 时段相关 3000+ ==========
    SCHEDULE_NOT_FOUND(3001, "时段不存在"),
    SCHEDULE_ALREADY_BOOKED(3002, "该时段已被预约"),
    SCHEDULE_NOT_AVAILABLE(3003, "该时段不可预约"),

    // ========== 订单相关 4000+ ==========
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态错误"),
    ORDER_CANNOT_CANCEL(4003, "订单不可取消");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
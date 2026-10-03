package com.counselor.common.constant;

/**
 * 通用常量
 */
public interface CommonConstant {

    /** Token 请求头 */
    String TOKEN_HEADER = "token";

    /** Redis Key 前缀 */
    String REDIS_PREFIX = "counselor:";

    /** 逻辑删除：未删除 */
    Integer NOT_DELETED = 0;

    /** 逻辑删除：已删除 */
    Integer DELETED = 1;

    /** 咨询师状态：下架 */
    Integer COUNSELOR_OFFLINE = 0;

    /** 咨询师状态：上架 */
    Integer COUNSELOR_ONLINE = 1;

    /** 时段状态：可约 */
    Integer SCHEDULE_AVAILABLE = 0;

    /** 时段状态：已约 */
    Integer SCHEDULE_BOOKED = 1;

    /** 时段状态：不可约 */
    Integer SCHEDULE_DISABLED = 2;

    /** 订单状态：待确认 */
    Integer ORDER_PENDING = 0;

    /** 订单状态：已确认 */
    Integer ORDER_CONFIRMED = 1;

    /** 订单状态：已完成 */
    Integer ORDER_COMPLETED = 2;

    /** 订单状态：已取消 */
    Integer ORDER_CANCELLED = 3;
}
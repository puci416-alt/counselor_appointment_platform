package com.counselor.service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("counselor")
public class Counselor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 姓名 */
    private String name;

    /** 头像 URL */
    private String avatar;

    /** 擅长领域，逗号分隔 */
    private String specialties;

    /** 职称 */
    private String title;

    /** 简介 */
    private String introduction;

    /** 资质 */
    private String qualification;

    /** 价格 */
    private BigDecimal price;

    /** 状态：0-下架 1-上架 */
    private Integer status;

    /** 排序权重 */
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
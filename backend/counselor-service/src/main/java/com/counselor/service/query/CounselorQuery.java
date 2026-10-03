package com.counselor.service.query;

import lombok.Data;

@Data
public class CounselorQuery {

    /** 当前页 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 擅长领域（模糊匹配） */
    private String specialty;

    /** 姓名（模糊匹配） */
    private String name;
}

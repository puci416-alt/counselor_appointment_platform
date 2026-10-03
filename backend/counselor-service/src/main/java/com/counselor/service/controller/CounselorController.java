package com.counselor.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.counselor.common.result.Result;
import com.counselor.service.entity.Counselor;
import com.counselor.service.query.CounselorQuery;
import com.counselor.service.service.CounselorService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counselor")
public class CounselorController {

    @Resource
    private CounselorService counselorService;

    /**
     * 分页查询咨询师列表
     */
    @GetMapping("/page")
    public Result<IPage<Counselor>> page(CounselorQuery query) {
        return Result.success(counselorService.pageQuery(query));
    }

    /**
     * 查询咨询师详情
     */
    @GetMapping("/{id}")
    public Result<Counselor> detail(@PathVariable Long id) {
        return Result.success(counselorService.getDetail(id));
    }
}

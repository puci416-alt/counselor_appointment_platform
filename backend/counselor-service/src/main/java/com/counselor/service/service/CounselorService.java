package com.counselor.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.counselor.service.entity.Counselor;
import com.counselor.service.query.CounselorQuery;

public interface CounselorService extends IService<Counselor> {

    /**
     * 分页查询上架咨询师
     */
    IPage<Counselor> pageQuery(CounselorQuery query);

    /**
     * 查询咨询师详情
     */
    Counselor getDetail(Long id);
}

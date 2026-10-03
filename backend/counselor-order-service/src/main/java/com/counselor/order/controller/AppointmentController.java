package com.counselor.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.counselor.common.context.UserContext;
import com.counselor.common.result.Result;
import com.counselor.order.entity.AppointmentOrder;
import com.counselor.order.query.AppointmentCreateCommand;
import com.counselor.order.service.AppointmentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Resource
    private AppointmentService appointmentService;

    /** 创建预约 */
    @PostMapping("/create")
    public Result<AppointmentOrder> create(@RequestBody @Valid AppointmentCreateCommand command) {
        Long userId = UserContext.getUserId();
        return Result.success(appointmentService.create(command, userId));
    }

    /** 取消预约 */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        appointmentService.cancel(id, userId);
        return Result.success();
    }

    /** 我的预约列表 */
    @GetMapping("/page")
    public Result<IPage<AppointmentOrder>> page(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        Long userId = UserContext.getUserId();
        return Result.success(appointmentService.pageByUser(userId, page, size));
    }

    @GetMapping("/test-limit")
    @SentinelResource(value = "testLimit", blockHandler = "handleBlock")
    public Result<String> testLimit() {
        return Result.success("ok");
    }

    public Result<String> handleBlock(BlockException ex) {
        return Result.error(429, "当前预约人数过多，请稍后再试");
    }

}

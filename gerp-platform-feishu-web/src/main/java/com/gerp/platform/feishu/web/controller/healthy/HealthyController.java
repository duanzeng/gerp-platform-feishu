package com.gerp.platform.feishu.web.controller.healthy;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zhanglibing
 * @Date: 2022-05-23 09:54
 * @Description:
 */
@RestController
@RequestMapping("/healthy/check")
@Api(value = "HealthyController", tags = "服务状态监测")
public class HealthyController {

    @ApiOperation(value = "服务状态监测", notes = "服务状态监测")
    @GetMapping
    public String healthy() {
        return "{code: 200}";
    }

}

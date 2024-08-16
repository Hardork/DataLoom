package com.hwq.dataloom.service.impl.inner;

import com.hwq.dataloom.framework.service.InnerHealthCheckService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author HWQ
 * @date 2024/8/14 15:00
 * @description
 */
@DubboService
public class InnerHealthCheckServiceImpl implements InnerHealthCheckService {
    @Override
    public String checkHealth() {
        return "admin-backend is alive";
    }
}

package com.hwq.dataloom.service.impl;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AIServiceImplTest {

    @Resource
    private AIServiceImpl aiService;

    @Resource
    private DashboardService dashboardService;

    @Test
    void testUserChatForSQL() {
        User user = new User();
        user.setId(1697633200786403330L);
        System.out.println(dashboardService.aiGenChart(2L, user));
    }
}


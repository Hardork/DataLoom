package com.hwq.dataloom.service.impl;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.ai.ChatForSQLRequest;
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

    @Test
    void testUserChatForSQL2() {
        User user = new User();
        user.setId(1697633200786403330L);
        ChatForSQLRequest chatForSQLRequest = new ChatForSQLRequest();
        chatForSQLRequest.setChatId(1863861078540382210L);
//        chatForSQLRequest.setQuestion("查询");
//        System.out.println(aiService.userChatForSQL());
    }
}


package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.ChatForSQLRequest;
import com.hwq.dataloom.model.entity.*;
import com.hwq.dataloom.service.ChatHistoryService;
import com.hwq.dataloom.service.ChatService;
import com.hwq.dataloom.service.CoreDatasetTableFieldService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import com.hwq.dataloom.websocket.AskSQLWebSocket;
import com.hwq.dataloom.websocket.vo.AskSQLWebSocketMsgVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@SpringBootTest
class AIServiceImplTest {
    @Mock
    ChatHistoryService chatHistoryService;
    @Mock
    DatasourceEngine datasourceEngine;
    @Mock
    ChatService chatService;
    @Mock
    AiManager aiManager;
    @Mock
    CoreDatasetTableFieldService coreDatasetTableFieldService;
    @Mock
    CoreDatasourceService coreDatasourceService;
    @Mock
    AskSQLWebSocket askSQLWebSocket;
    @InjectMocks
    AIServiceImpl aIServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserChatForSQL() {

    }
}


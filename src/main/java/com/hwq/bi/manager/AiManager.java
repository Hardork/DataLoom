package com.hwq.bi.manager;

import cn.hutool.core.collection.CollUtil;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.model.entity.Chart;
import com.hwq.bi.model.enums.RoleEnum;
import com.hwq.bi.utils.Message;
import com.hwq.bi.utils.MoonshotAiUtils;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 用于对接 AI 平台
 */
@Service
public class AiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;


    /**
     * AI 对话
     *
     * @param modelId
     * @param message
     * @return
     */
    public String doChat(long modelId, String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();
    }

    public String doChatWithKimi(String message) {
        String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容： \n" +
                "分析需求： {数据分析的需求或者目标} \n" +
                "原始数据： {csv格式的原始数据，用,作为分隔符} \n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象js代码，要求option是json格式的,例如{\n" +
                "  \"xAxis\": {\n" +
                "    \"type\": \"category\",\n" +
                "    \"data\": [\"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\"]\n" +
                "  },\n" +
                "  \"yAxis\": {\n" +
                "    \"type\": \"value\"\n" +
                "  },\n" +
                "  \"series\": [\n" +
                "    {\n" +
                "      \"data\": [0, 0, 0, 0, 0, 0, 0, 0, 0],\n" +
                "      \"type\": \"line\"\n" +
                "    }\n" +
                "  ]\n" +
                "}，注意可视化数据不要超过10条，超过10条可以仅仅返回关键的数据的可视化配置，合理地将数据进行可视化，不要生成任何多余的内容，比如注释} \n" +
                "【【【【【 \n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), message)
        );
        return MoonshotAiUtils.chat("moonshot-v1-128k",messages);
    }

}

package com.hwq.dataloom.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.enums.RoleEnum;
import com.hwq.dataloom.utils.Message;
import com.hwq.dataloom.utils.MoonshotAiClient;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用于对接 AI 平台
 */
@Service
public class AiManager {

    @Resource
    private MoonshotAiClient moonshotAiClient;


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
        return moonshotAiClient.chat("moonshot-v1-128k",messages);
    }

    /**
     * 执行智能问数
     * @param message 构造的输入
     * @param limitSize select 结果限制的行数
     * @return
     */
    public String doAskSQLWithKimi(String message, int limitSize) {
        String SQLPrompt = "你是一个MySQL数据库专家，专门负责根据查询需求得出SQL查询语句，接下来我会按照以下固定格式给你提供内容： \n" +
                "分析需求:{分析需求或者目标} \n" +
                "所有的数据表元数据:[{数据库表名、表注释、数据库表的字段、注释以及类型}] \n" +
                "请根据这两部分内容，按照以下指定格式生成内容(此外不要输出任何多余的开头、结尾、注释),并且只生成Select语句!!!， 请严格按照数据表元数据中存在的数据表和字段，不要查询不存在的表和字段\n" +
                "要求select的结果不超过" + limitSize + "行";
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), SQLPrompt),
                new Message(RoleEnum.user.name(), message)
        );
        return moonshotAiClient.chat("moonshot-v1-32k",messages);
    }

    /**
     * 执行图表分析
     * @param type
     * @param dataOption
     * @param seriesDataListJsonStr
     * @param xArrayDataJsonStr
     * @return
     */
    public String doAskChartAnalysis(String type, String dataOption, String seriesDataListJsonStr, String xArrayDataJsonStr, boolean isFlux, User loginUser) {
        String prompt = "现在你是一名图表分析专家\n" +
                "下面我将给出图表的类型、图表数据请求配置（其中dataTableName表示数据表的名称，seriesArray代表数据列也就图表纵轴的数据，fieldName表示字段的名称，rollup表示数据分组统计的函数，group表示分组的字段）以及查询出的图表数据(seriesDataList表示图表数值数据，xarrayData表示横轴数据)\n" +
                "请返回图表的分析报告(包含洞察与建议、具体分析、数据概括)，要求格式为md格式：\n" +
                "### 洞察与建议\n" +
                "### 具体分析\n" +
                "### 数据概括";
        String message = String.format(
                "图表类型：%s\n" +
                "图表数据请求配置：%s\n" +
                "图表数据：%s\n",
                type,
                dataOption,
                seriesDataListJsonStr + xArrayDataJsonStr
                );
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), message)
        );
        if (isFlux) { // 流获取
            return moonshotAiClient.chatFlux("moonshot-v1-32k",messages, loginUser);
        }
        return moonshotAiClient.chat("moonshot-v1-32k",messages);
    }

    /**
     * 执行图表分析
     * @param datasourceMetaInfo
     * @return
     */
    public String doAiGenChartByDatasource(String datasourceMetaInfo, boolean isFlux, User loginUser) {
        String prompt = "现在你是一名图表分析专家\n" +
                "下面我将给出数据源的元信息, 请你返回所有你认为有价值的图表数据配置" +
                "所有的数据表元数据:[{数据库表名、表注释、数据库表的字段、注释以及类型}] \n" +
                "请返回图表的数据配置，示例格式为以下的JSON数组：\n" +
                "[{\n" +
                "  \"chartType\":\"当前图表类型\",\n" +
                "  \"chartName\":\"当前图表名称\",\n" +
                "  \"dataTableName\":\"数据库表名\",\n" +
                "  \"seriesArray\":[\n" +
                "    {\"fieldName\":\"数据库表字段\",\"rollup\":\"计算函数\"}\n" +
                "  ],\n" +
                "  \"group\":[\n" +
                "    {\"fieldName\":\"数据库表字段\"}\n" +
                "  ]\n" +
                "}]" +
                "其中rollup为分组检索的聚合函数有COUNT、MAX、MIN、SUM四种计算类型，chartType有line、pie、scatter、bar，返回的JSON数组长度不超过3"
                ;
        String message = String.format(
                        "数据源元数据：%s\n",
                datasourceMetaInfo
        );
        List<Message> messages = CollUtil.newArrayList(
                new Message(RoleEnum.system.name(), prompt),
                new Message(RoleEnum.user.name(), message)
        );
        if (isFlux) { // 流获取
            return moonshotAiClient.chatFlux("moonshot-v1-32k",messages, loginUser);
        }
        return moonshotAiClient.chat("moonshot-v1-32k",messages);
    }


}

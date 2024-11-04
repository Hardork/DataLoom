package com.hwq.dataloom.mq.consumer;

import com.github.rholder.retry.*;
import com.hwq.dataloom.constant.MessageRouteConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.ws.vo.WebSocketMsgVO;
import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.AnalysisChartByAIRequest;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.entity.Chart;
import com.hwq.dataloom.model.entity.FailedChart;
import com.hwq.dataloom.model.entity.UserMessage;
import com.hwq.dataloom.model.enums.ChartStatusEnum;
import com.hwq.dataloom.model.enums.UserMessageTypeEnum;
import com.hwq.dataloom.framework.model.enums.WebSocketMsgTypeEnum;
import com.hwq.dataloom.mq.constant.AnalysisMqConstant;
import com.hwq.dataloom.service.*;
import com.hwq.dataloom.service.basic.handler.AITaskChainContext;
import com.hwq.dataloom.utils.datasource.MongoEngineUtils;
import com.hwq.dataloom.websocket.UserWebSocket;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

@Component
@Slf4j
public class AnalysisMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @Resource
    private UserWebSocket userWebSocket;

    @Resource
    private FailedChartService failedChartService;

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private AIService aiService;

    @Resource
    private UserService userService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private AITaskChainContext aiTaskChainContext;

    /**
     * 监听BI队列消息，交由KimiAI处理
     * @param message 消息
     * @param channel 通道
     * @param deliveryTag tag标识
     */
    @SneakyThrows
    @RabbitListener(queues = {AnalysisMqConstant.GEN_VIP_CHART_NAME}, ackMode = "MANUAL", containerFactory = "kimiContainerFactory")
    public void receiveMessageToKimi(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("KimiAI receiveMessage message = {}", message);
        channel.basicQos(1);
        if (StringUtils.isBlank(message)) {
            // 如果失败，消息拒绝, 并且不返回队列中
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        // 消息确认
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }
        // 修改图表任务状态
        if (!updateChartRunning(channel, deliveryTag, chart)) return;

        User userInfo = userService.getById(chart.getUserId());
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests = aiService.getAskAIWithDataTablesAndFieldsRequests(userInfo, chart.getDatasourceId());
        // 构造查询数据的SQL
        String input = aiService.buildAskAISQLInput(dataTablesAndFieldsRequests, chart.getGoal());
        AnalysisChartByAIRequest aiRequest = AnalysisChartByAIRequest.builder()
                .userQuestion(chart.getGoal())
                .curRes(input)
                .build();
        aiTaskChainContext.handle("analysis_chart", aiRequest);
        // TODO: 处理input
        // 超时重试机制
        Retryer<String[]> retryer = getRetryer(2, 10, TimeUnit.SECONDS);
        String[] result;
        try {
            result =  retryer.call(() -> { // 提交任务
                String chatRes = aiManager.doChatWithKimi(input);
                return chatRes.split("【【【【【");
            });
        } catch (RetryException e) { // 重试器抛出异常，说明重试了两次还是失败了,设置失败
            // 将任务设置为失败，不再重新排队
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "AI生成错误，请检查文件内容，如有异常请联系管理员");
            return;
        }

        // 提炼结果
        String genChart = result[1].trim(); // 生成的图表option
        String genResult = result[2].trim(); // 生成的分析结果
        // 更新图表状态为succeed
        updateChartSucceed(channel, deliveryTag, chart, genChart, genResult);

        // 通知入库
        savaToUserMessage(chart);

        // 通知用户操作成功
        notifyUserSucceed(chartId, chart);

        // 手动确认
        channel.basicAck(deliveryTag, false);
    }



    /**
     * 监听死信队列
     * 进入死信队列的一般是队列达到最长长度（队列满了)
     * 消息TTL到了（消息过期）
     * 分析原因：一般是当前提问的用户太多
     * 解决措施：
     * 1.告诉用户当前正忙，稍后再试，返还用户积分
     * 2.等队列不忙了，再去消费
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(queues = {AnalysisMqConstant.BI_DEAD_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveDeadMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
            // 如果失败，消息拒绝
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
        }

        // 已成功
        if (chart.getStatus().equals(ChartStatusEnum.SUCCEED.getValue())) {
            return;
        }

        // 更新图表的信息为超时
        updateChartTimeOut(chart);

        // 失败信息入库 给管理员看
        saveFailedChart(chartId, chart);

        // 通知入库
        savaToUserMessage(chart);

        // 通知用户分析失败
        notifyUserFailed(chart);

        // 消息确认
        channel.basicAck(deliveryTag, false);
    }


    /**
     * 通知用户
     * @param chart 图表信息
     */
    private void savaToUserMessage(Chart chart) {
        UserMessage userMessage = new UserMessage();
        userMessage.setTitle("分析图表已生成");
        userMessage.setUserId(chart.getUserId());
        userMessage.setDescription("点击查看详情");
        userMessage.setType(UserMessageTypeEnum.SUCCESS.getValue());
        userMessage.setRoute(MessageRouteConstant.CHART_DETAIL + chart.getId());
        userMessage.setIsRead(0);
        boolean save = userMessageService.save(userMessage);
        if (!save) {
            log.error("系统异常");
        }
    }

    /**
     * 使用websocket通知前端图表生成成功
     * @param chartId
     * @param chart
     */
    private void notifyUserSucceed(long chartId, Chart chart) {
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setType(WebSocketMsgTypeEnum.SUCCESS.getValue());
        webSocketMsgVO.setTitle("生成图表成功");
        webSocketMsgVO.setDescription("点击查看详情");
        webSocketMsgVO.setChartId(chartId + "");
        userWebSocket.sendOneMessage(chart.getUserId(), webSocketMsgVO);
    }

    private void updateChartSucceed(Channel channel, long deliveryTag, Chart chart, String genChart, String genResult) throws IOException {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getValue());
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }
    }


    /**
     * 自定义重试器
     * @param attemptNumber 最多的尝试次数
     * @param sleepTime 尝试间隔时间
     * @param timeUnit 时间单位
     * @return 重试器
     */
    private static Retryer<String[]> getRetryer(int attemptNumber, int sleepTime, TimeUnit timeUnit) {
        return RetryerBuilder.<String[]>newBuilder()
                .retryIfResult(list -> list.length < 3)
                .withWaitStrategy(WaitStrategies.fixedWait(sleepTime, timeUnit))
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber))
                .build();
    }


    /**
     * 更新图表状态
     * @param channel 通道
     * @param deliveryTag
     * @param chart
     * @return
     * @throws IOException
     */
    private boolean updateChartRunning(Channel channel, long deliveryTag, Chart chart) throws IOException {
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            return false;
        }
        return true;
    }


    private void notifyUserFailed(Chart chart) {
        log.error("分析超时" + chart.getId());
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setType(WebSocketMsgTypeEnum.ERROR.getValue());
        webSocketMsgVO.setTitle("生成图表失败");
        webSocketMsgVO.setDescription("失败原因：系统正忙，请稍后再试");
        userWebSocket.sendOneMessage(chart.getUserId(), webSocketMsgVO);
    }

    private void saveFailedChart(long chartId, Chart chart) {
        FailedChart failedChart = new FailedChart();
        failedChart.setChartId(chartId);
        failedChart.setStatus(ChartStatusEnum.TIMEOUT.getValue());
        failedChart.setExecMessage("系统繁忙");
        failedChart.setUserId(chart.getUserId());
        failedChartService.save(failedChart);
    }

    private void updateChartTimeOut(Chart chart) {
        Chart updateChartStatus = new Chart();
        updateChartStatus.setId(chart.getId());
        updateChartStatus.setStatus(ChartStatusEnum.TIMEOUT.getValue());
        updateChartStatus.setExecMessage("系统繁忙，将在空闲时间重试执行，请耐心等待");
        boolean update = chartService.updateById(updateChartStatus);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
    }


    private void handleChartUpdateError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage("execMessage");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
        }
    }

}
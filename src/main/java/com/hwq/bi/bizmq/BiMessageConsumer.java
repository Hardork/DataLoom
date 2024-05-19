package com.hwq.bi.bizmq;

import cn.hutool.json.JSONUtil;
import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.constant.ChartConstant;
import com.hwq.bi.constant.CommonConstant;
import com.hwq.bi.constant.ExecuteAIServiceConstant;
import com.hwq.bi.constant.MessageRouteConstant;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.manager.AiManager;
import com.hwq.bi.model.entity.Chart;
import com.hwq.bi.model.entity.FailedChart;
import com.hwq.bi.model.entity.ProductOrder;
import com.hwq.bi.model.entity.UserMessage;
import com.hwq.bi.model.enums.ChartStatusEnum;
import com.hwq.bi.model.enums.OrderStatusEnum;
import com.hwq.bi.model.enums.UserMessageTypeEnum;
import com.hwq.bi.model.enums.WebSocketMsgTypeEnum;
import com.hwq.bi.service.ChartService;
import com.hwq.bi.service.FailedChartService;
import com.hwq.bi.service.ProductOrderService;
import com.hwq.bi.service.UserMessageService;
import com.hwq.bi.utils.ExcelUtils;
import com.hwq.bi.websocket.UserWebSocket;
import com.hwq.bi.websocket.vo.WebSocketMsgVO;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.*;

@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @Resource
    private UserWebSocket userWebSocket;

    @Resource
    private FailedChartService failedChartService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private ExcelUtils excelUtils;

    /**
     * 监听BI队列消息，并调用ChatGPT接口进行消费
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL", containerFactory = "gptContainerFactory")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("ChatGPT receiveMessage message = {}", message);
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
        // 修改图表任务状态为 “执行中”
        // 执行成功后，修改为 “已完成”、保存执行结果；
        // 执行失败后，状态修改为 “失败”，记录任务失败信息。
        if (!updateChartRunning(channel, deliveryTag, chart)) return;

        String input = buildUserInputFromMongo(chart);

        // 超时重试机制
        // 定义重试器
        Retryer<String[]> retryer = getRetryer();

        String[] result;
        try {
            result =  retryer.call(new Callable<String[]>() {
                @Override
                public String[] call() throws Exception { // 提交任务
                    String chatRes = aiManager.doChat(CommonConstant.BI_MODEL_ID, input);
                    return chatRes.split("【【【【【");
                }
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

        // 将图表数据缓存到redis中去
        saveToRedis(chartId, chart, genChart, genResult);

        // 通知入库
        savaToUserMessage(chart);

        // 通知用户操作成功
        notifyUserSucceed(chartId, chart);

        // 手动确认
        channel.basicAck(deliveryTag, false);
    }


    /**
     * 监听BI队列消息，交由KimiAI处理
     * 从mongoDB中取数据
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_VIP_QUEUE_NAME}, ackMode = "MANUAL", containerFactory = "kimiContainerFactory")
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
        // 修改图表任务状态为 “执行中”
        // 执行成功后，修改为 “已完成”、保存执行结果；
        // 执行失败后，状态修改为 “失败”，记录任务失败信息。
        if (!updateChartRunning(channel, deliveryTag, chart)) return;

        // 超时重试机制
        // 定义重试器
        Retryer<String[]> retryer = getRetryer();
        String input = buildUserInputFromMongo(chart);
        // todo：根据策略模式去选择对应的分析模型
        // 分析的依据：
        // 用户的身份 普通用户：8K以下

        // 用户的选择

        String[] result;
        try {
            result =  retryer.call(new Callable<String[]>() {
                @Override
                public String[] call() throws Exception { // 提交任务
                    String chatRes = aiManager.doChatWithKimi(input);
                    return chatRes.split("【【【【【");
                }
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

        // 将图表数据缓存到redis中去
        saveToRedis(chartId, chart, genChart, genResult);

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
    @RabbitListener(queues = {BiMqConstant.BI_DEAD_QUEUE_NAME}, ackMode = "MANUAL")
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


    // 自定义重试器
    private static Retryer<String[]> getRetryer() {
        // 返回结果不符合预期就重试
        // 重试时间固定为10s一次
        // 允许重试3次
        return RetryerBuilder.<String[]>newBuilder()
                .retryIfResult(list -> list.length < 3)
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(2))
                .build();
    }

    /**
     * 将图表缓存到Redis
     * @param chartId
     * @param chart
     * @param genChart
     * @param genResult
     */
    private void saveToRedis(long chartId, Chart chart, String genChart, String genResult) {
        String key = ChartConstant.CHART_PREFIX + chartId;
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setStatus(ChartStatusEnum.SUCCEED.getValue());
        String chartJson = JSONUtil.toJsonStr(chart);

        // 缓存时间1h
        redisTemplate.opsForValue().set(key, chartJson, 60*60, TimeUnit.SECONDS);
    }

    /**
     * 更新图表状态
     * @param channel
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

    /**
     * 将用户存储在mongo中的数据转为构造的input
     * @param chart
     * @return
     */
    private String buildUserInputFromMongo(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        Long userDataId = chart.getUserDataId();
        String csvData = excelUtils.mongoToCSV(userDataId);
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
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
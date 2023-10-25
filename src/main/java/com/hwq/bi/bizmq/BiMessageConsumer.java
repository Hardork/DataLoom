package com.hwq.bi.bizmq;

import cn.hutool.json.JSONUtil;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.constant.ChartConstant;
import com.hwq.bi.constant.CommonConstant;
import com.hwq.bi.constant.ExecuteAIServiceConstant;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.manager.AiManager;
import com.hwq.bi.model.entity.Chart;
import com.hwq.bi.model.entity.FailedChart;
import com.hwq.bi.model.entity.ProductOrder;
import com.hwq.bi.model.enums.ChartStatusEnum;
import com.hwq.bi.model.enums.OrderStatusEnum;
import com.hwq.bi.model.enums.WebSocketMsgTypeEnum;
import com.hwq.bi.service.ChartService;
import com.hwq.bi.service.FailedChartService;
import com.hwq.bi.service.ProductOrderService;
import com.hwq.bi.websocket.UserWebSocket;
import com.hwq.bi.websocket.WebSocketMsgVO;
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
    private ProductOrderService productOrderService;

    @Resource
    private FailedChartService failedChartService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
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
        // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
            return;
        }

        // 调用 AI
        // 设置超时任务
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                // 这里是你的任务代码
                return aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(chart));
            }
        });
        String result = "";
        try {
            // 设置超时时间为2MIN
            result = future.get(ExecuteAIServiceConstant.LIMIT_TIME, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // 更新图表的信息为失败
            Chart updateChartStatus = new Chart();
            updateChartStatus.setId(chart.getId());
            updateChartStatus.setStatus(ChartStatusEnum.FAILED.getValue());
            boolean update = chartService.updateById(updateChartStatus);
            ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
            // 通知用户
            log.error("分析超时" + chart.getUserId());
            WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
            webSocketMsgVO.setType(WebSocketMsgTypeEnum.ERROR.getValue());
            webSocketMsgVO.setTitle("生成图表失败");
            webSocketMsgVO.setDescription("失败原因：生成图表时间超时");
            userWebSocket.sendOneMessage(chart.getUserId(), webSocketMsgVO);
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程
            executor.shutdown();
        }
        // 校验返回的参数
        if (StringUtils.isEmpty(result)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "AI 生成错误");
            return;
        }

        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getValue());
        boolean updateResult = chartService.updateById(updateChartResult);

        // 讲数据缓存到redis中去
        String key = ChartConstant.CHART_PREFIX + chartId;
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setStatus(ChartStatusEnum.SUCCEED.getValue());
        String chartJson = JSONUtil.toJsonStr(chart);

        // 缓存时间1h
        redisTemplate.opsForValue().set(key, chartJson, 60*60, TimeUnit.SECONDS);

        if (!updateResult) {
            channel.basicNack(deliveryTag, false, false);
            handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
        }
        channel.basicAck(deliveryTag, false);
        // 通知用户操作成功
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setType(WebSocketMsgTypeEnum.SUCCESS.getValue());
        webSocketMsgVO.setTitle("生成图表成功");
        webSocketMsgVO.setDescription("点击查看详情");
        webSocketMsgVO.setChartId(chartId + "");
        userWebSocket.sendOneMessage(chart.getUserId(), webSocketMsgVO);
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

        // 更新图表的信息为失败
        Chart updateChartStatus = new Chart();
        updateChartStatus.setId(chart.getId());
        updateChartStatus.setStatus(ChartStatusEnum.FAILED.getValue());
        updateChartStatus.setExecMessage("系统繁忙");
        boolean update = chartService.updateById(updateChartStatus);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);

        // 失败信息入库 给管理员看
        FailedChart failedChart = new FailedChart();
        failedChart.setChartId(chartId);
        failedChart.setStatus(ChartStatusEnum.FAILED.getValue());
        failedChart.setExecMessage("系统繁忙");
        failedChart.setUserId(chart.getUserId());
        failedChartService.save(failedChart);


        // 通知用户分析失败
        log.error("分析超时" + chart.getId());
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setType(WebSocketMsgTypeEnum.ERROR.getValue());
        webSocketMsgVO.setTitle("生成图表失败");
        webSocketMsgVO.setDescription("失败原因：系统正忙，请稍后再试");
        userWebSocket.sendOneMessage(chart.getUserId(), webSocketMsgVO);
        // 消息确认
        channel.basicAck(deliveryTag, false);
    }



    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.ORDER_DEAD_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveOrderMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        if (StringUtils.isBlank(message)) {
            // 如果失败，消息拒绝
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        long orderId = Long.parseLong(message);
        ProductOrder order = productOrderService.getById(orderId);
        if (order == null) {
            channel.basicAck(deliveryTag, false);
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }

        // 更新订单信息为超时（前提是订单的状态为未支付）
        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getEnumByValue(order.getStatus());
        ThrowUtils.throwIf(orderStatusEnum == null, ErrorCode.SYSTEM_ERROR);
        if (orderStatusEnum.equals(OrderStatusEnum.NOT_PAY)) { // 改为超时
            order.setStatus(OrderStatusEnum.TIMEOUT.getValue());
            boolean update = productOrderService.updateById(order);
            ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        }
        log.error("用户订单超时");
        // 通知用户
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setType(WebSocketMsgTypeEnum.ERROR.getValue());
        webSocketMsgVO.setTitle("订单超时");
        webSocketMsgVO.setDescription("失败原因：超时未支付");
        userWebSocket.sendOneMessage(order.getUserId(), webSocketMsgVO);
        // 消息确认
        channel.basicAck(deliveryTag, false);
    }



    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

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

package com.hwq.bi.controller;

/**
 * @Author:HWQ
 * @DateTime:2023/9/1 23:42
 * @Description:
 **/

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.hwq.bi.annotation.BiService;
import com.hwq.bi.annotation.CheckPoint;
import com.hwq.bi.annotation.ReduceRewardPoint;
import com.hwq.bi.bizmq.BiMessageProducer;
import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.DeleteRequest;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;

import com.hwq.bi.constant.ChartConstant;
import com.hwq.bi.constant.CommonConstant;
import com.hwq.bi.constant.MessageRouteConstant;
import com.hwq.bi.manager.AiManager;
import com.hwq.bi.manager.RedisLimiterManager;
import com.hwq.bi.model.dto.chart.*;
import com.hwq.bi.model.entity.Chart;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.annotation.AuthCheck;
import com.hwq.bi.constant.UserConstant;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.entity.UserMessage;
import com.hwq.bi.model.enums.ChartStatusEnum;
import com.hwq.bi.model.enums.UserMessageTypeEnum;
import com.hwq.bi.model.enums.WebSocketMsgTypeEnum;
import com.hwq.bi.model.vo.BiResponse;
import com.hwq.bi.service.ChartService;
import com.hwq.bi.service.UserMessageService;
import com.hwq.bi.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hwq.bi.utils.ExcelUtils;
import com.hwq.bi.websocket.BudgeWebSocket;
import com.hwq.bi.websocket.UserWebSocket;
import com.hwq.bi.websocket.WebSocketMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private BiMessageProducer biMessageProducer;

    @Resource
    private UserMessageService userMessageService;

    // websocket 给用户发消息
    @Resource
    private UserWebSocket userWebSocket;

    @Resource
    private BudgeWebSocket budgeWebSocket;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private final static Gson GSON = new Gson();


    /**
     * 生成图表
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    @ReduceRewardPoint(reducePoint = 2)
    @CheckPoint(needPoint = 2)
    @BiService
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件后缀
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffix = Arrays.asList("xlsx");
        ThrowUtils.throwIf(!validFileSuffix.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
        // 校验文件大小
        final long ONE_MB = 1024*1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1MB");

        // 限流，防止用户在同一时间段内多次请求该接口
        String key = "chart:gen:" + loginUser.getId();
        redisLimiterManager.doRateLimit(key);
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        // 构造用户输入
        String userInput = buildUserInput(goal, chartType, csvData);

        long biModelId = CommonConstant.BI_MODEL_ID;
        String result = aiManager.doChat(biModelId, userInput);
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    /**
     * 异步生成图表
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async")
    @ReduceRewardPoint(reducePoint = 2)
    @CheckPoint(needPoint = 2)
    @BiService
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                                 GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
        // 校验文件后缀 aaa.png
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");

        User loginUser = userService.getLoginUser(request);
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
        long biModelId = 1659171950288818178L;

        String csvData = ExcelUtils.excelToCsv(multipartFile);
        // 构造用户输入
        String userInput = buildUserInput(goal, chartType, csvData);

        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus(ChartStatusEnum.WAIT.getValue());
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // todo 建议处理任务队列满了后，抛异常的情况
        CompletableFuture.runAsync(() -> {
            // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
            Chart updateChart = new Chart();
            updateChart.setId(chart.getId());
            updateChart.setStatus(ChartStatusEnum.RUNNING.getValue());
            boolean b = chartService.updateById(updateChart);
            if (!b) {
                handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
                return;
            }
            // 调用 AI
            String result = aiManager.doChat(biModelId, userInput);
            String[] splits = result.split("【【【【【");
            if (splits.length < 3) {
                handleChartUpdateError(chart.getId(), "AI 生成错误");
                return;
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();
            Chart updateChartResult = new Chart();
            updateChartResult.setId(chart.getId());
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            // todo 建议定义状态为枚举值
            updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getValue());
            boolean updateResult = chartService.updateById(updateChartResult);
            if (!updateResult) {
                handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
            }
            // 生成消息，存入数据库
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

            // 通知用户图表已生成
            WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
            // 将chart.getId()变为字符串是为了防止大数精度丢失问题
            webSocketMsgVO.setChartId(chart.getId() + "");
            webSocketMsgVO.setTitle("分析图表已生成");
            webSocketMsgVO.setDescription("点击查看详情");
            webSocketMsgVO.setType(WebSocketMsgTypeEnum.SUCCESS.getValue());
            userWebSocket.sendOneMessage(loginUser.getId(), webSocketMsgVO);
            // 显示徽标
            budgeWebSocket.sendOneMessage(loginUser.getId(), "用户有新的消息");

        }, threadPoolExecutor);

        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);

    }

    @PostMapping("/gen/async/mq")
    @ReduceRewardPoint(reducePoint = 2)
    @BiService
    @CheckPoint(needPoint = 2)
    public BaseResponse<BiResponse> genChartByAiAsyncMq(@RequestPart("file") MultipartFile multipartFile,
                                                        GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
        // 校验文件后缀 aaa.png
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls", "csv");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
        User loginUser = userService.getLoginUser(request);
        // 判断用户积分是否充足
        Integer totalRewardPoints = loginUser.getTotalRewardPoints();
        // 限流判断，每个用户一个限流器 每秒最多访问 2 次
        ThrowUtils.throwIf(totalRewardPoints <= 0, ErrorCode.OPERATION_ERROR, "积分不足");
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
//        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
//                "分析需求：\n" +
//                "{数据分析的需求或者目标}\n" +
//                "原始数据：\n" +
//                "{csv格式的原始数据，用,作为分隔符}\n" +
//                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
//                "【【【【【\n" +
//                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
//                "【【【【【\n" +
//                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        long biModelId = 1659171950288818178L;
        // 压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        // 防止投喂给AI的数据太大
        ThrowUtils.throwIf(csvData.length() > 2048, ErrorCode.PARAMS_ERROR, "文件字数超过2048字");

        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        long newChartId = chart.getId();
        biMessageProducer.sendMessage(String.valueOf(newChartId));
        // 分析成功
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return ResultUtils.success(biResponse);
    }


    @PostMapping("/gen/retry")
    @ReduceRewardPoint(reducePoint = 2)
    @CheckPoint(needPoint = 2)
    public BaseResponse<BiResponse> ReGenChartByAiAsync(@RequestBody ReGenChartRequest reGenChartRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(reGenChartRequest == null , ErrorCode.PARAMS_ERROR);
        Long chartId = reGenChartRequest.getChartId();
        ThrowUtils.throwIf( chartId == null , ErrorCode.PARAMS_ERROR);


        // 获取用户信息
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 限流判断，每个用户一个限流器
        redisLimiterManager.doRateLimit("reGenChartByAi_" + loginUser.getId());
        // 获取原来图表的信息 并且只有失败图表才能重试
        Chart chartInfo = chartService.getById(chartId);
        ThrowUtils.throwIf(chartInfo == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!chartInfo.getStatus().equals(ChartStatusEnum.FAILED.getValue()), ErrorCode.PARAMS_ERROR, "仅失败图表可重试");
        // 发送信息给消息队列
        biMessageProducer.sendMessage(String.valueOf(chartId));
        // 分析成功
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chartId);
        return ResultUtils.success(biResponse);
    }

    private String buildUserInput(String goal, String chartType, String csvData) {
        // 分析需求：
        // 分析网站用户的增长情况
        // 原始数据：
        // 日期,用户数
        // 1号,10
        // 2号,20
        // 3号,30
        // 拼接分析目标
        //        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
        //                "分析需求：\n" +
        //                "{数据分析的需求或者目标}\n" +
        //                "原始数据：\n" +
        //                "{csv格式的原始数据，用,作为分隔符}\n" +
        //                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
        //                "【【【【【\n" +
        //                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
        //                "【【【【【\n" +
        //                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
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
        // 压缩后的数据
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


    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        chartService.validChart(chart, true);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newchartId = chart.getId();
        return ResultUtils.success(newchartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        // 参数校验
        chartService.validChart(chart, false);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询redis中对应的缓存
        String chartJson = redisTemplate.opsForValue().get(ChartConstant.CHART_PREFIX + id);
        Chart chart = null;

        if (StringUtils.isNotEmpty(chartJson)) { //缓存命中
            chart = JSONUtil.toBean(chartJson, Chart.class);
        } else {
            chart = chartService.getById(id);
        }

        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }


    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Chart>> listChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }


    /**
     * 编辑（表）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        // 参数校验
        chartService.validChart(chart, false);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }


}

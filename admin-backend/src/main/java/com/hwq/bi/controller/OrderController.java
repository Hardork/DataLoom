package com.hwq.bi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.order.OrderAddRequest;
import com.hwq.bi.model.dto.order.OrderCancelRequest;
import com.hwq.bi.model.dto.order.OrderPayRequest;
import com.hwq.bi.model.dto.order.OrderQueryRequest;
import com.hwq.bi.model.entity.ProductOrder;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.enums.OrderStatusEnum;
import com.hwq.bi.service.ProductOrderService;
import com.hwq.bi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/10/11 16:12
 * @Description:
 **/
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Resource
    private UserService userService;

    @Resource
    private ProductOrderService productOrderService;
    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(orderAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long id = productOrderService.addOrder(orderAddRequest, loginUser);
        return ResultUtils.success(id);
    }


    @PostMapping("/list/page")
    public BaseResponse<Page<ProductOrder>> listUserOrderByPage(@RequestBody OrderQueryRequest orderQueryRequest,
                                                   HttpServletRequest request) {
        long current = orderQueryRequest.getCurrent();
        long size = orderQueryRequest.getPageSize();
        QueryWrapper<ProductOrder> queryWrapper = productOrderService.getQueryWrapper(orderQueryRequest);
        queryWrapper.eq("userId", userService.getLoginUser(request).getId());
        Page<ProductOrder> orderPage = productOrderService.page(new Page<>(current, size),queryWrapper);
        return ResultUtils.success(orderPage);
    }

    @GetMapping("/get/byId")
    public BaseResponse<ProductOrder> getUserOrderById(Long id,
                                                                HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ProductOrder productOrder = productOrderService.getById(id);
        return ResultUtils.success(productOrder);
    }

    @PostMapping("/pay")
    public BaseResponse<Boolean> userPayOrder(@RequestBody OrderPayRequest orderPayRequest,
                                                                HttpServletRequest request) {
        Long id = orderPayRequest.getId();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        Boolean res = productOrderService.payOrder(id);
        return ResultUtils.success(res);
    }

    @PostMapping("/cancel")
    public BaseResponse<Long> userCancelOrder(@RequestBody OrderCancelRequest orderCancelRequest,
                                                         HttpServletRequest request) {
        // 判断用户取消是否是自己的订单
        User loginUser = userService.getLoginUser(request);
        Long id = orderCancelRequest.getId();
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        ProductOrder order = productOrderService.getById(id);
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");
        ThrowUtils.throwIf(!order.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        ThrowUtils.throwIf(OrderStatusEnum.SUCCESS.equals(OrderStatusEnum.getEnumByValue(order.getStatus())), ErrorCode.PARAMS_ERROR, "订单已支付");
        ThrowUtils.throwIf(OrderStatusEnum.CANCEL.equals(OrderStatusEnum.getEnumByValue(order.getStatus())), ErrorCode.PARAMS_ERROR, "订单已取消");
        ThrowUtils.throwIf(OrderStatusEnum.TIMEOUT.equals(OrderStatusEnum.getEnumByValue(order.getStatus())), ErrorCode.PARAMS_ERROR, "订单已过期");
        order.setStatus(OrderStatusEnum.CANCEL.getValue());
        boolean update = productOrderService.updateById(order);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(id);
    }
}

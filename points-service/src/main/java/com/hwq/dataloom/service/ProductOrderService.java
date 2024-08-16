package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.model.dto.order.OrderAddRequest;
import com.hwq.dataloom.model.dto.order.OrderQueryRequest;
import com.hwq.dataloom.model.entity.ProductOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.framework.model.entity.User;

/**
* @author HWQ
* @description 针对表【product_order(商品订单)】的数据库操作Service
* @createDate 2023-10-11 22:37:21
*/
public interface ProductOrderService extends IService<ProductOrder> {

    /**
     * 添加订单
     * @param orderAddRequest
     * @param loginUser
     * @return
     */
    Long addOrder(OrderAddRequest orderAddRequest, User loginUser);

    QueryWrapper<ProductOrder> getQueryWrapper(OrderQueryRequest orderQueryRequest);

    Boolean payOrder(Long id);
}

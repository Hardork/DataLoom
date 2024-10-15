package com.hwq.dataloom.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.model.entity.UserCreditAccount;
import com.hwq.dataloom.framework.request.PageRequest;
import com.hwq.dataloom.framework.service.InnerMemberAccountService;
import com.hwq.dataloom.framework.service.InnerUserCreditAccountService;
import com.hwq.dataloom.product.mapper.ProductMapper;
import com.hwq.dataloom.product.mapper.ProductOrderMapper;
import com.hwq.dataloom.product.model.dto.ProductDTO;
import com.hwq.dataloom.product.model.dto.ProductOrderDTO;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.model.entity.ProductOrder;
import com.hwq.dataloom.product.model.request.ProductOrderRequest;
import com.hwq.dataloom.product.model.vo.OrderStateVO;
import com.hwq.dataloom.product.service.ProductOrderService;
import com.hwq.dataloom.product.service.ProductService;
import com.hwq.dataloom.product.service.impl.product.chain.IActionChain;
import com.hwq.dataloom.product.service.impl.product.factory.ProductChainFactory;
import org.apache.dubbo.config.annotation.DubboReference;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
        implements ProductOrderService {

    @Resource
    private ProductService productService;

    @DubboReference
    private InnerUserCreditAccountService innerUserCreditAccountService;

    @Resource
    protected ProductChainFactory productChainFactory;


    @Override
    public void createProductOrder(ProductOrderRequest productOrderRequest) {
        // 1. 查询商品
        Product product = productService.getById(productOrderRequest.getProductId());
        // todo 查询优惠券

        // 2. 账户额度校验
        UserCreditAccount userCreditAccount = innerUserCreditAccountService.queryUserCreditAccount(productOrderRequest.getUserId());
        if(userCreditAccount.getAvailableAmount().compareTo(product.getProductAmount())<0){
            // 账户额度不足
            return ;
        }
        // 3. 校验商品合法性
        IActionChain actionChain = productChainFactory.openActionChain();
        actionChain.action(product);
        // 4. 订单创建
        ProductOrder productOrder = new ProductOrder();
        productOrder.setProductId(product.getId());
        productOrder.setPointId(null);
        productOrder.setPayAmount(product.getProductAmount());
        productOrder.setState(OrderStateVO.wait_pay.getCode());
        save(productOrder);
        // 5. 异步生效

    }
}

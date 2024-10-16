package com.hwq.dataloom.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.model.entity.UserCreditAccount;
import com.hwq.dataloom.framework.service.InnerUserCreditAccountService;
import com.hwq.dataloom.product.mapper.ProductOrderMapper;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.model.entity.ProductOrder;
import com.hwq.dataloom.product.model.request.ProductOrderRequest;
import com.hwq.dataloom.product.model.vo.OrderStateVO;
import com.hwq.dataloom.product.mq.event.ProductDistributeEvent;
import com.hwq.dataloom.product.mq.producer.ProductDistributeProducer;
import com.hwq.dataloom.product.service.ProductOrderService;
import com.hwq.dataloom.product.service.ProductService;
import com.hwq.dataloom.product.service.impl.product.rule.chain.IActionChain;
import com.hwq.dataloom.product.service.impl.product.rule.factory.ProductChainFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
        implements ProductOrderService {

    @Resource
    private ProductService productService;

    @DubboReference
    private InnerUserCreditAccountService innerUserCreditAccountService;

    @Resource
    protected ProductChainFactory productChainFactory;

    @Resource
    private ProductDistributeProducer productDistributeProducer;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public void createProductOrder(ProductOrderRequest productOrderRequest) {
        // 1. 查询商品
        CompletableFuture<Product> productFuture = CompletableFuture.supplyAsync(() -> productService.getById(productOrderRequest.getProductId()));

        CompletableFuture<?> pointFuture = CompletableFuture.supplyAsync(() -> {
            // todo 查询优惠券
            return null;
        });
        CompletableFuture<UserCreditAccount> accountFuture = CompletableFuture.supplyAsync(() -> {
            // 2. 账户额度校验
            return innerUserCreditAccountService.queryUserCreditAccount(productOrderRequest.getUserId());
        });
        // 等待两个异步操作都完成
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(pointFuture, accountFuture, productFuture);
        combinedFuture.thenRun(() -> {
            Product product = productFuture.join();
            Object point = pointFuture.join();
            UserCreditAccount userCreditAccount = accountFuture.join();
            // todo 根据优惠券和商品计算价格。
            BigDecimal payAmount = new BigDecimal("1");
            if (userCreditAccount.getAvailableAmount().compareTo(payAmount) < 0) {
                // 账户额度不足
                return;
            }

            // 3. 校验商品合法性
            IActionChain actionChain = productChainFactory.openActionChain();
            actionChain.action(product);
            // 4. 订单创建
            ProductOrder productOrder = new ProductOrder();
            productOrder.setProductId(product.getId());
            // todo
            productOrder.setPointId(null);
            // todo
            productOrder.setPayAmount(product.getProductAmount());
            productOrder.setState(OrderStateVO.wait_pay.getCode());
            productOrder.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
            save(productOrder);
            // 5. 异步生效
            threadPoolExecutor.execute(() -> {
                productDistributeProducer.sendMessage(ProductDistributeEvent.builder()
                        .outBusinessNo(productOrder.getOutBusinessNo())
                        .product(product)
                        .userId(productOrderRequest.getUserId())
                        .build());
            });
        });


    }
}

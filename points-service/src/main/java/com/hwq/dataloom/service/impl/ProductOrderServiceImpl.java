package com.hwq.dataloom.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.constants.ProductOrderConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.service.InnerUserServiceInterface;
import com.hwq.dataloom.model.dto.order.OrderAddRequest;
import com.hwq.dataloom.model.dto.order.OrderQueryRequest;
import com.hwq.dataloom.model.entity.ProductOrder;
import com.hwq.dataloom.model.entity.ProductPoint;
import com.hwq.dataloom.model.entity.ProductVip;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.enums.OrderStatusEnum;
import com.hwq.dataloom.model.enums.OrderTypeEnum;
import com.hwq.dataloom.model.enums.ProductInfoStatusEnum;
import com.hwq.dataloom.model.enums.UserRoleEnum;
import com.hwq.dataloom.model.vo.ProductInfo;
import com.hwq.dataloom.mapper.ProductOrderMapper;
import com.hwq.dataloom.mq.producer.OrderMessageProducer;
import com.hwq.dataloom.service.ProductOrderService;
import com.hwq.dataloom.service.ProductPointService;
import com.hwq.dataloom.service.ProductVipService;
import com.hwq.dataloom.utils.MoneyUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
* @author HWQ
* @description 针对表【product_order(商品订单)】的数据库操作Service实现
* @createDate 2023-10-11 22:37:21
*/
@Service
public class ProductOrderServiceImpl extends ServiceImpl<ProductOrderMapper, ProductOrder>
    implements ProductOrderService {

    @Resource
    private ProductPointService productPointService;

    @Resource
    private ProductVipService productVipService;

    @DubboReference
    private InnerUserServiceInterface userService;

    @Resource
    private OrderMessageProducer orderMessageProducer;

    @Override
    public Long addOrder(OrderAddRequest orderAddRequest, User loginUser) {
        Long productId = orderAddRequest.getProductId();
        Integer productType = orderAddRequest.getProductType();
        ThrowUtils.throwIf(productId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(productType == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ProductInfo productInfo = getProductInfoByTypeAndId(productId, productType);
        // 查询用户是否还有类似订单未支付，如果有返回未支付订单的id
        LambdaQueryWrapper<ProductOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductOrder::getUserId, loginUser.getId());
        queryWrapper.eq(ProductOrder::getProductType, productType);
        queryWrapper.eq(ProductOrder::getProductId, productId);
        queryWrapper.eq(ProductOrder::getStatus, OrderStatusEnum.NOT_PAY.getValue());
        ProductOrder preOrder = this.getOne(queryWrapper);
        if (preOrder != null) {
            return preOrder.getId();
        }
        String name = productInfo.getName();
        Long total = productInfo.getTotal();
        Long addPoints = productInfo.getAddPoints();
        // 5分钟有效期
        Date date = DateUtil.date(System.currentTimeMillis());
        Date expirationTime = DateUtil.offset(date, DateField.MINUTE, 5);
        // 存入数据库
        ProductOrder productOrder = new ProductOrder();
        productOrder.setOrderNo(ProductOrderConstant.ORDER_PREFIX + RandomUtil.randomNumbers(20));
        productOrder.setUserId(loginUser.getId());
        productOrder.setProductId(productId);
        productOrder.setOrderName(name);
        productOrder.setTotal(total);
        productOrder.setProductType(productType);
        productOrder.setAddPoints(addPoints);
        productOrder.setStatus(OrderStatusEnum.NOT_PAY.getValue());
        productOrder.setExpirationTime(expirationTime);
        ThrowUtils.throwIf(!this.save(productOrder), ErrorCode.SYSTEM_ERROR);
        // 将订单推送到延迟队列，定时过期
        orderMessageProducer.sendOrderMessage(productOrder.getId().toString());
        return productOrder.getId();
    }

    @Override
    public QueryWrapper<ProductOrder> getQueryWrapper(OrderQueryRequest orderQueryRequest) {
         String orderNo = orderQueryRequest.getOrderNo();
         Long productId = orderQueryRequest.getProductId();
         String orderName = orderQueryRequest.getOrderName();
         Long total = orderQueryRequest.getTotal();
         Integer productType = orderQueryRequest.getProductType();
         String status = orderQueryRequest.getStatus();
         String payType = orderQueryRequest.getPayType();
         Long addPoints = orderQueryRequest.getAddPoints();
        QueryWrapper<ProductOrder> qw = new QueryWrapper<>();
        qw.eq(StringUtils.isNotEmpty(orderNo), "orderNo", orderNo);
        qw.eq(StringUtils.isNotEmpty(orderName), "orderName", orderName);
        qw.eq(StringUtils.isNotEmpty(status), "status", status);
        qw.eq(StringUtils.isNotEmpty(payType), "payType", payType);
        qw.eq(ObjectUtils.isNotEmpty(productId), "productId", productId);
        qw.eq(ObjectUtils.isNotEmpty(total), "total", MoneyUtils.saveToDatabaseMoney(total));
        qw.eq(ObjectUtils.isNotEmpty(productType), "productType", productType);
        qw.eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints);
        qw.eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints);
        qw.orderByDesc("createTime");
        return qw;
    }

    @Override
    @Transactional
    public Boolean payOrder(Long id) {
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        ProductOrder order = this.getById(id);
        ThrowUtils.throwIf(order == null, ErrorCode.PARAMS_ERROR, "订单不存在");
        ThrowUtils.throwIf(OrderStatusEnum.SUCCESS.equals(OrderStatusEnum.getEnumByValue(order.getStatus())), ErrorCode.PARAMS_ERROR, "订单已支付");
        ThrowUtils.throwIf(OrderStatusEnum.CANCEL.equals(OrderStatusEnum.getEnumByValue(order.getStatus())), ErrorCode.PARAMS_ERROR, "订单已取消");
        ThrowUtils.throwIf(OrderStatusEnum.TIMEOUT.equals(OrderStatusEnum.getEnumByValue(order.getStatus())), ErrorCode.PARAMS_ERROR, "订单已过期");
        // 更新订单支付状态
        UpdateWrapper<ProductOrder> qw = new UpdateWrapper<>();
        qw.set("status", OrderStatusEnum.SUCCESS.getValue());
        boolean update = this.update(qw);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);

        ProductInfo productInfo = getProductInfoByTypeAndId(order.getProductId(), order.getProductType());
        Long userId = order.getUserId();
        User userInfo = userService.getById(userId);
        ThrowUtils.throwIf(userInfo == null, ErrorCode.NOT_LOGIN_ERROR);

        // 如果订单是积分服务，就给用户添加积分
        if (OrderTypeEnum.POINT_TYPE.equals(OrderTypeEnum.getEnumByValue(order.getProductType()))) {
            Boolean updateUser = userService.updateUserTotalRewardPoint(userId, userInfo.getTotalRewardPoints() + productInfo.getAddPoints());
            ThrowUtils.throwIf(!updateUser, ErrorCode.SYSTEM_ERROR);
        }

        // 管理员不需要经过VIP服务
        if (UserRoleEnum.ADMIN.equals(UserRoleEnum.getEnumByValue(userInfo.getUserRole()))) {
            return true;
        }

        // 如果订单是会员服务，就给用更换会员身份
        if (OrderTypeEnum.VIP_TYPE.equals(OrderTypeEnum.getEnumByValue(order.getProductType()))) {
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.eq("id", userId);
            userUpdateWrapper.set("userRole", UserRoleEnum.VIP.getValue());
            Date vipExpirationTime = userInfo.getVIPExpirationTime();
            if (ObjectUtils.isNotEmpty(vipExpirationTime)) { // 判断用户当前是否是VIP
                boolean isVIP = new Date().before(vipExpirationTime);
                // 是VIP在原有基础上添加
                userUpdateWrapper.set(isVIP, "VIPExpirationTime", addDate(vipExpirationTime, 30));
                // 不是VIP在当前时间添加
                userUpdateWrapper.set(!isVIP, "VIPExpirationTime", addDate(new Date(), 30));
            } else {
                // 不是VIP在当前时间添加
                userUpdateWrapper.set("VIPExpirationTime", addDate(new Date(), 30));
            }
            boolean updateUser = userService.update(userUpdateWrapper);
            ThrowUtils.throwIf(!updateUser, ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }

    private Date addDate(Date date, int i){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH ,i); //把日期往后增加i天,整数  往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推i天的结果
        return date;
    }


    public ProductInfo getProductInfoByTypeAndId(Long productId, Integer productType) {
        ProductInfo productInfo = new ProductInfo();
        // 获取对应类型的商品信息
        OrderTypeEnum orderType = OrderTypeEnum.getEnumByValue(productType);
        ThrowUtils.throwIf(orderType == null, ErrorCode.PARAMS_ERROR);
        if (orderType.equals(OrderTypeEnum.POINT_TYPE)) {// 积分订单
            // 查询积分商品信息
            LambdaQueryWrapper<ProductPoint> qw = new LambdaQueryWrapper<>();
            qw.eq(ProductPoint::getId, productId);
            qw.eq(ProductPoint::getStatus, ProductInfoStatusEnum.ONLINE.getValue());
            ProductPoint productPoint = productPointService.getOne(qw);
            ThrowUtils.throwIf(productPoint == null, ErrorCode.PARAMS_ERROR, "不存在该商品");
            productInfo.setName(productPoint.getName());
            productInfo.setTotal(MoneyUtils.getRealMoney(productPoint.getTotal()));
            productInfo.setAddPoints(productPoint.getAddPoints());
        }

        if (orderType.equals(OrderTypeEnum.VIP_TYPE)) {// 积分订单
            // 查询VIP商品信息
            LambdaQueryWrapper<ProductVip> qw = new LambdaQueryWrapper<>();
            qw.eq(ProductVip::getId, productId);
            qw.eq(ProductVip::getStatus, ProductInfoStatusEnum.ONLINE.getValue());
            ProductVip productVip = productVipService.getOne(qw);
            ThrowUtils.throwIf(productVip == null, ErrorCode.PARAMS_ERROR, "不存在该商品");
            productInfo.setName(productVip.getName());
            productInfo.setTotal(MoneyUtils.getRealMoney(productVip.getTotal()));
            productInfo.setAddPoints(productVip.getAddPoints());
        }
        return productInfo;
    }
}





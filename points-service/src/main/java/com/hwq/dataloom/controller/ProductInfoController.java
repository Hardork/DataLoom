package com.hwq.dataloom.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.annotation.AuthCheck;
import com.hwq.dataloom.framework.constants.UserConstant;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.request.DeleteRequest;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.service.InnerUserServiceInterface;
import com.hwq.dataloom.model.dto.product_info.*;
import com.hwq.dataloom.model.entity.ProductPoint;
import com.hwq.dataloom.model.entity.ProductVip;
import com.hwq.dataloom.model.enums.OrderTypeEnum;
import com.hwq.dataloom.model.enums.ProductInfoStatusEnum;
import com.hwq.dataloom.model.vo.GetProductPointInfoByTypeVO;
import com.hwq.dataloom.service.ProductPointService;
import com.hwq.dataloom.service.ProductVipService;
import com.hwq.dataloom.utils.MoneyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Author:HWQ
 * @DateTime:2023/10/10 8:45
 * @Description: 商品信息接口
 **/
@RestController
@RequestMapping("/productInfo")
@Slf4j
public class ProductInfoController {
    @Resource
    private ProductPointService productPointService;

    @Resource
    private ProductVipService productVipService;

    @DubboReference
    private InnerUserServiceInterface userService;


    // region 增删改查

    /**
     * 添加接口信息
     * 创建
     *
     * @param productInfoAddRequest 接口信息添加请求
     * @param request               请求
     */
    @PostMapping("/add/point")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addProductPointInfo(@RequestBody ProductPointAddRequest productInfoAddRequest, HttpServletRequest request) {
        if (productInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductPoint productPoint = new ProductPoint();
        BeanUtils.copyProperties(productInfoAddRequest, productPoint);
        // 校验
        productPointService.validProductInfo(productPoint, true);
        User loginUser = userService.getLoginUser(request);
        productPoint.setUserId(loginUser.getId());
        productPoint.setTotal(MoneyUtils.saveToDatabaseMoney(productInfoAddRequest.getTotal()));
        productPoint.setOriginalTotal(MoneyUtils.saveToDatabaseMoney(productInfoAddRequest.getOriginalTotal()));
        boolean result = productPointService.save(productPoint);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newProductInfoId = productPoint.getId();
        return ResultUtils.success(newProductInfoId);
    }

    @PostMapping("/add/vip")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addProductVipInfo(@RequestBody ProductVipAddRequest productInfoAddRequest, HttpServletRequest request) {
        if (productInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductVip productVip = new ProductVip();
        BeanUtils.copyProperties(productInfoAddRequest, productVip);
        // 校验
        productVipService.validProductInfo(productVip, true);
        User loginUser = userService.getLoginUser(request);
        productVip.setUserId(loginUser.getId());
        productVip.setTotal(MoneyUtils.saveToDatabaseMoney(productInfoAddRequest.getTotal()));
        productVip.setOriginalTotal(MoneyUtils.saveToDatabaseMoney(productInfoAddRequest.getOriginalTotal()));
        boolean result = productVipService.save(productVip);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newProductInfoId = productVip.getId();
        return ResultUtils.success(newProductInfoId);
    }

    /**
     * 删除接口信息
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete/point")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteProductPointInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ProductPoint oldProductInfo = productPointService.getById(id);
        if (oldProductInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean b = productPointService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 删除接口信息
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete/vip")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteProductVipInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        ProductVip oldProductInfo = productVipService.getById(id);
        if (oldProductInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean b = productVipService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新接口信息
     * 更新
     *
     * @param productInfoUpdateRequest 接口信息更新请求
     * @param request                  请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/update/point")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateProductPointInfo(@RequestBody ProductPointUpdateRequest productInfoUpdateRequest,
                                                   HttpServletRequest request) {
        if (ObjectUtils.anyNull(productInfoUpdateRequest, productInfoUpdateRequest.getId()) || productInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductPoint productInfo = new ProductPoint();
        BeanUtils.copyProperties(productInfoUpdateRequest, productInfo);
        // 参数校验
        productPointService.validProductInfo(productInfo, false);
        long id = productInfoUpdateRequest.getId();
        // 判断是否存在
        ProductPoint oldProductInfo = productPointService.getById(id);
        if (oldProductInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        productInfo.setTotal(MoneyUtils.saveToDatabaseMoney(productInfo.getTotal()));
        productInfo.setOriginalTotal(MoneyUtils.saveToDatabaseMoney(productInfo.getOriginalTotal()));
        boolean result = productPointService.updateById(productInfo);
        return ResultUtils.success(result);
    }

    @PostMapping("/update/vip")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateProductVipInfo(@RequestBody ProductVipUpdateRequest productInfoUpdateRequest,
                                                   HttpServletRequest request) {
        if (ObjectUtils.anyNull(productInfoUpdateRequest, productInfoUpdateRequest.getId()) || productInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductVip productInfo = new ProductVip();
        BeanUtils.copyProperties(productInfoUpdateRequest, productInfo);
        // 参数校验
        productVipService.validProductInfo(productInfo, false);
        long id = productInfoUpdateRequest.getId();
        // 判断是否存在
        ProductVip oldProductInfo = productVipService.getById(id);
        if (oldProductInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        productInfo.setTotal(MoneyUtils.saveToDatabaseMoney(productInfo.getTotal()));
        productInfo.setOriginalTotal(MoneyUtils.saveToDatabaseMoney(productInfo.getOriginalTotal()));
        boolean result = productVipService.updateById(productInfo);
        return ResultUtils.success(result);
    }

    /**
     * 通过id获取接口信息
     *
     * @param id id
     */
    @GetMapping("/get/point")
    public BaseResponse<ProductPoint> getProductPointInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductPoint productInfo = productPointService.getById(id);
        productInfo.setTotal(MoneyUtils.getRealMoney(productInfo.getTotal()));
        productInfo.setOriginalTotal(MoneyUtils.getRealMoney(productInfo.getOriginalTotal()));
        return ResultUtils.success(productInfo);
    }


    @PostMapping("/getByType")
    public BaseResponse<GetProductPointInfoByTypeVO> getProductPointInfoByType(@RequestBody GetByTypeRequest getByTypeRequest) {
        ThrowUtils.throwIf(getByTypeRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = getByTypeRequest.getId();
        Integer type = getByTypeRequest.getType();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(type == null, ErrorCode.PARAMS_ERROR);
        OrderTypeEnum orderType = OrderTypeEnum.getEnumByValue(getByTypeRequest.getType());
        ThrowUtils.throwIf(orderType == null, ErrorCode.PARAMS_ERROR, "不存在该商品");
        GetProductPointInfoByTypeVO res = new GetProductPointInfoByTypeVO();
        if (orderType.equals(OrderTypeEnum.POINT_TYPE)) {// 积分订单
            // 查询积分商品信息
            LambdaQueryWrapper<ProductPoint> qw = new LambdaQueryWrapper<>();
            qw.eq(ProductPoint::getId, id);
            qw.eq(ProductPoint::getStatus, ProductInfoStatusEnum.ONLINE.getValue());
            ProductPoint productPoint = productPointService.getOne(qw);
            ThrowUtils.throwIf(productPoint == null, ErrorCode.PARAMS_ERROR, "不存在该商品");
            res.setId(productPoint.getId());
            res.setName(productPoint.getName());
            res.setDescription(productPoint.getDescription());
            res.setTotal(MoneyUtils.getRealMoney(productPoint.getTotal()));
        }

        if (orderType.equals(OrderTypeEnum.VIP_TYPE)) {// 积分订单
            // 查询VIP商品信息
            LambdaQueryWrapper<ProductVip> qw = new LambdaQueryWrapper<>();
            qw.eq(ProductVip::getId, id);
            qw.eq(ProductVip::getStatus, ProductInfoStatusEnum.ONLINE.getValue());
            ProductVip productVip = productVipService.getOne(qw);
            ThrowUtils.throwIf(productVip == null, ErrorCode.PARAMS_ERROR, "不存在该商品");
            res.setId(productVip.getId());
            res.setName(productVip.getName());
            res.setDescription(productVip.getDescription());
            res.setTotal(MoneyUtils.getRealMoney(productVip.getTotal()));
        }
        return ResultUtils.success(res);
    }

    /**
     * 通过id获取接口信息
     *
     * @param id id
     */
    @GetMapping("/get/vip")
    public BaseResponse<ProductVip> getProductVipInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductVip productInfo = productVipService.getById(id);
        productInfo.setTotal(MoneyUtils.getRealMoney(productInfo.getTotal()));
        productInfo.setOriginalTotal(MoneyUtils.getRealMoney(productInfo.getOriginalTotal()));
        return ResultUtils.success(productInfo);
    }


    /**
     * 分页获取列表
     *
     * @param productInfoQueryRequest 接口信息查询请求
     * @param request                 请求
     */
    @GetMapping("/list/point/page")
    public BaseResponse<Page<ProductPoint>> listProductPointInfoByPage(ProductPointQueryRequest productInfoQueryRequest, HttpServletRequest request) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductPoint productInfoQuery = new ProductPoint();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);
        long size = productInfoQueryRequest.getPageSize();
        String sortField = productInfoQueryRequest.getSortField();
        String sortOrder = productInfoQueryRequest.getSortOrder();

        String name = productInfoQueryRequest.getName();
        long current = productInfoQueryRequest.getCurrent();
        String description = productInfoQueryRequest.getDescription();
        Long addPoints = productInfoQueryRequest.getAddPoints();
        Long total = productInfoQueryRequest.getTotal();
        Integer status = productInfoQuery.getStatus();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<ProductPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name)
                .like(StringUtils.isNotBlank(description), "description", description)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total)
                .eq(ObjectUtils.isNotEmpty(status), "status", status)
        ;
        // 根据金额升序排列
        queryWrapper.orderByAsc("total");
        Page<ProductPoint> productInfoPage = productPointService.page(new Page<>(current, size), queryWrapper);

        productInfoPage.getRecords().stream().map(productPoint -> {
            productPoint.setTotal(MoneyUtils.getRealMoney(productPoint.getTotal()));
            productPoint.setOriginalTotal(MoneyUtils.getRealMoney(productPoint.getOriginalTotal()));
            return productPoint;
        }).collect(Collectors.toList());

        // 不是管理员只能查看已经上线的
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            List<ProductPoint> productInfoList = productInfoPage.getRecords().stream()
                    .filter(productInfo -> productInfo.getStatus().equals(ProductInfoStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
            productInfoPage.setRecords(productInfoList);
        }
        return ResultUtils.success(productInfoPage);
    }

    @GetMapping("/list/vip/page")
    public BaseResponse<Page<ProductVip>> listProductVipInfoByPage(ProductVipQueryRequest productInfoQueryRequest, HttpServletRequest request) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ProductVip productInfoQuery = new ProductVip();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);
        long size = productInfoQueryRequest.getPageSize();
        String name = productInfoQueryRequest.getName();
        long current = productInfoQueryRequest.getCurrent();
        String description = productInfoQueryRequest.getDescription();
        Long addPoints = productInfoQueryRequest.getAddPoints();
        Long total = productInfoQueryRequest.getTotal();
        Integer status = productInfoQuery.getStatus();
        Integer productType = productInfoQuery.getProductType();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<ProductVip> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name)
                .like(StringUtils.isNotBlank(description), "description", description)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total)
                .eq(ObjectUtils.isNotEmpty(status), "status", status)
                .eq(ObjectUtils.isNotEmpty(productType),"productType", productType);
        // 根据金额升序排列
        queryWrapper.orderByAsc("total");
        Page<ProductVip> productInfoPage = productVipService.page(new Page<>(current, size), queryWrapper);

        productInfoPage.getRecords().stream().map(productPoint -> {
            productPoint.setTotal(MoneyUtils.getRealMoney(productPoint.getTotal()));
            productPoint.setOriginalTotal(MoneyUtils.getRealMoney(productPoint.getOriginalTotal()));
            return productPoint;
        }).collect(Collectors.toList());

        // 不是管理员只能查看已经上线的
        User loginUser = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            List<ProductVip> productInfoList = productInfoPage.getRecords().stream()
                    .filter(productInfo -> productInfo.getStatus().equals(ProductInfoStatusEnum.ONLINE.getValue())).collect(Collectors.toList());
            productInfoPage.setRecords(productInfoList);
        }
        return ResultUtils.success(productInfoPage);
    }

    /**
     * 发布
     *
     * @param idRequest id请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/online/point")
    public BaseResponse<Boolean> onlineProductPointInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        ProductPoint productInfo = productPointService.getById(id);
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductInfoStatusEnum.ONLINE.getValue());
        return ResultUtils.success(productPointService.updateById(productInfo));
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/online/vip")
    public BaseResponse<Boolean> onlineProductVipInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        ProductVip productInfo = productVipService.getById(id);
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductInfoStatusEnum.ONLINE.getValue());
        return ResultUtils.success(productVipService.updateById(productInfo));
    }

    /**
     * 下线
     *
     * @param idRequest id请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/offline/point")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineProductPointInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        ProductPoint productInfo = productPointService.getById(id);
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductInfoStatusEnum.OFFLINE.getValue());
        return ResultUtils.success(productPointService.updateById(productInfo));
    }

    @PostMapping("/offline/vip")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineProductVipInfo(@RequestBody IdRequest idRequest) {
        if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        ProductVip productInfo = productVipService.getById(id);
        if (productInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductInfoStatusEnum.OFFLINE.getValue());
        return ResultUtils.success(productVipService.updateById(productInfo));
    }
}

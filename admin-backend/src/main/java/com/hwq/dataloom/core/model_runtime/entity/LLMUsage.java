package com.hwq.dataloom.core.model_runtime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author HWQ
 * @date 2024/12/7 23:58
 * @description 大模型使用计费
 */
@Data
@AllArgsConstructor
public class LLMUsage {

    // 提示令牌数
    private int promptTokens;
    // 提示单价
    private BigDecimal promptUnitPrice;
    // 提示价格单位
    private BigDecimal promptPriceUnit;
    // 提示价格
    private BigDecimal promptPrice;
    // 完成令牌数
    private int completionTokens;
    // 完成单价
    private BigDecimal completionUnitPrice;
    // 完成价格单位
    private BigDecimal completionPriceUnit;
    // 完成价格
    private BigDecimal completionPrice;
    // 总令牌数
    private int totalTokens;
    // 总价格
    private BigDecimal totalPrice;
    // 货币
    private String currency;
    // 延迟
    private float latency;

    /**
     * 空用法的构造函数
     *
     * @return LLMUsage 的空实例
     */
    public static LLMUsage emptyUsage() {
        return new LLMUsage(
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0,
                BigDecimal.ZERO,
                "RMB",
                0.0F
        );
    }

    /**
     * 将两个 LLMUsage 实例相加
     *
     * @param other 要添加的另一个 LLMUsage 实例
     * @return 具有总和值的新 LLMUsage 实例
     */
    public LLMUsage plus(LLMUsage other) {
        if (this.totalTokens == 0) {
            return other;
        } else {
            return new LLMUsage(
                    this.promptTokens + other.promptTokens,
                    other.promptUnitPrice,
                    other.promptPriceUnit,
                    this.promptPrice.add(other.promptPrice),
                    this.completionTokens + other.completionTokens,
                    other.completionUnitPrice,
                    other.completionPriceUnit,
                    this.completionPrice.add(other.completionPrice),
                    this.totalTokens + other.totalTokens,
                    this.totalPrice.add(other.totalPrice),
                    other.currency,
                    this.latency + other.latency
            );
        }
    }

    /**
     * 重载 + 运算符以添加两个 LLMUsage 实例
     * @param other 要添加的另一个 LLMUsage 实例
     * @return 具有总和值的新 LLMUsage 实例
     */
    public LLMUsage add(LLMUsage other) {
        return this.plus(other);
    }
}

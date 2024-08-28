package com.hwq.dataloom.mq.wrapper;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author HWQ
 * @date 2024/8/26 17:41
 * @description 消息包装类
 */
@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class MessageWrapper<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息发送 Key
     */
    @NonNull
    private String key;

    /**
     * 消息体
     */
    @NonNull
    private T message;

    /**
     * 唯一标识，用于客户端幂等验证
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 消息发送时间
     */
    private Long timestamp = System.currentTimeMillis();
}

package com.hwq.dataloom.utils.generator;

import java.util.function.Consumer;

/**
 * @author HWQ
 * @date 2024/12/8 23:08
 * @description 流序列
 */
public interface Seq<T> {
    void consume(Consumer<T> consumer);
}

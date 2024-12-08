package com.hwq.dataloom.core.workflow.node.handler.test;

import java.util.Iterator;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author HWQ
 * @date 2024/12/8 23:08
 * @description 流序列
 */
public interface Seq<T> {
    String consume(Consumer<T> consumer);

    default String join(String sep) {
        StringJoiner joiner = new StringJoiner(sep);
        consume(t -> joiner.add(t.toString()));
        return joiner.toString();
    }

    static <T> T stop() {
        throw StopException.INSTANCE;
    }

    default <E, R> Seq<R> zip(Iterable<E> iterable, BiFunction<T, E, R> function) {
        return c -> {
            Iterator<E> iterator = iterable.iterator();
            consumeTillStop(t -> {
                if (iterator.hasNext()) {
                    c.accept(function.apply(t, iterator.next()));
                } else {
                    stop();
                }
            });
            return null;
        };
    }

    default void consumeTillStop(Consumer<T> consumer) {
        try {
            consume(consumer);
        } catch (StopException ignore) {}
    }

}

package com.hwq.dataloom.core.workflow.node.handler.test;

import com.hwq.dataloom.utils.generator.Generator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * @author HWQ
 * @date 2024/12/8 20:31
 * @description 测试生成器方法
 */
public class Test {
    public static void main(String[] args) {
        Stream<String> generate = run();
        generate.forEach(event -> {
            // 处理消息
            System.out.println("main方法模拟处理消息:"+ event);
            System.out.println("返回消息" + event);
        });
    }

    public static Stream<String> run() {
        return Generator.stream(c -> {

            c.accept("start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            c.accept("do task1");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            c.accept("do task2");
        });
    }


}

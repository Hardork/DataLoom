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
        Stream<String> stream = Generator.stream(c -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            c.accept("start");
            c.accept("end");
        });
        System.out.println("开始处理啦");
        stream.forEach(integer -> {
            if (integer.equals("start")) {
                System.out.println("异常发生");
                return;
            }
        });
    }


}

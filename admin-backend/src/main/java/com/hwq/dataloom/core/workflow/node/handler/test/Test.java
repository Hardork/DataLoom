package com.hwq.dataloom.core.workflow.node.handler.test;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author HWQ
 * @date 2024/12/8 20:31
 * @description
 */
public class Test {
}

class NumberGenerator implements Iterator<Integer> {

    private int current = 0;
    private final int limit = 5;

    @Override
    public boolean hasNext() {
        return current < limit;
    }

    @Override
    public Integer next() {
        if (hasNext()) {
            int num = current;
            current++;
            System.out.println("hello");
            return num;
        }
        throw new NoSuchElementException();
    }

    public static void main(String[] args) {

        NumberGenerator gen = new NumberGenerator();
        while (gen.hasNext()) {
            System.out.println(gen.next());
        }
    }
}

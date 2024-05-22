package com.hwq.bi.utils.retry;


import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class CustomRetryer<T> {
    /**
     * 重试条件
     */
    private final Predicate<T> retryIfResult;
    /**
     * 等待策略
     */
    private final WaitStrategy waitStrategy;
    /**
     * 停止策略
     */
    private final StopStrategy stopStrategy;


    private CustomRetryer(Predicate<T> retryIfResult, WaitStrategy waitStrategy, StopStrategy stopStrategy) {
        this.retryIfResult = retryIfResult;
        this.waitStrategy = waitStrategy;
        this.stopStrategy = stopStrategy;
    }

    /**
     * 真正执行任务的代码
     * @param callable
     * @return
     * @throws Exception
     */
    public T call(Callable<T> callable) throws Exception {
        int attempt = 0;
        long startTime = System.currentTimeMillis();

        while (true) {
            // 增加尝试次数
            attempt++;
            T result = callable.call();
            // 判断是否符合返回条件
            if (!retryIfResult.test(result) || stopStrategy.shouldStop(attempt, System.currentTimeMillis() - startTime)) {
                return result;
            }

            waitStrategy.waitForNextAttempt();
        }
    }

    /**
     * 建造者模式
     * @return
     * @param <T>
     */
    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private Predicate<T> retryIfResult;
        private WaitStrategy waitStrategy;
        private StopStrategy stopStrategy;

        public Builder<T> retryIfResult(Predicate<T> retryIfResult) {
            this.retryIfResult = retryIfResult;
            return this;
        }

        public Builder<T> withWaitStrategy(WaitStrategy waitStrategy) {
            this.waitStrategy = waitStrategy;
            return this;
        }

        public Builder<T> withStopStrategy(StopStrategy stopStrategy) {
            this.stopStrategy = stopStrategy;
            return this;
        }

        public CustomRetryer<T> build() {
            return new CustomRetryer<>(retryIfResult, waitStrategy, stopStrategy);
        }
    }

    /**
     * 等待策略接口
     */
    public interface WaitStrategy {
        /**
         * 等待指定时间再次重试
         * @throws InterruptedException
         */
        void waitForNextAttempt() throws InterruptedException;
    }

    /**
     * 停止策略接口
     */
    public interface StopStrategy {
        /**
         * 停止重试条件
         * @param attempt
         * @param elapsedTime
         * @return
         */
        boolean shouldStop(int attempt, long elapsedTime);
    }

    /**
     * 等待策略实现类
     */
    public static class FixedWaitStrategy implements WaitStrategy {
        private final long waitTime;
        private final TimeUnit timeUnit;

        public FixedWaitStrategy(long waitTime, TimeUnit timeUnit) {
            this.waitTime = waitTime;
            this.timeUnit = timeUnit;
        }

        @Override
        public void waitForNextAttempt() throws InterruptedException {
            timeUnit.sleep(waitTime);
        }
    }

    /**
     * 停止策略实现类
     */
    public static class StopAfterAttemptStrategy implements StopStrategy {
        private final int maxAttempts;

        public StopAfterAttemptStrategy(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        @Override
        public boolean shouldStop(int attempt, long elapsedTime) {
            return attempt >= maxAttempts;
        }
    }
}

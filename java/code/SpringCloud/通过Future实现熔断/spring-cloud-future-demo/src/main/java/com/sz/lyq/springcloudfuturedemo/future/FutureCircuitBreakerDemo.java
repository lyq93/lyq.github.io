package com.sz.lyq.springcloudfuturedemo.future;

import java.util.Random;
import java.util.concurrent.*;

public class FutureCircuitBreakerDemo {

    public static void main(String[] args) {
        //初始化线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        FutureCommand futureCommand = new FutureCommand();
        //提交任务获取future对象
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return futureCommand.run();
            }
        });

        String result = null;
        try {
            //通过异常捕获的方式实现熔断
            result = future.get(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            result = futureCommand.fallBack();
        }

        System.out.println(result);

        executorService.shutdown();

    }


    /**
     * 随机对象
     */
    private static final Random random = new Random();

    /**
     * 接口实现
     */
    public static class FutureCommand implements Command<String> {
        @Override
        public String run() throws Exception {

            long executeTimes = random.nextInt(200);

            System.out.println("executeTimes time:" + executeTimes);

            Thread.sleep(executeTimes);

            return "hello world";
        }

        @Override
        public String fallBack() {
            return "fallback";
        }
    }

    /**
     * 类似于hystrix的接口
     * @param <T>
     */
    public static interface Command<T> {
        /**
         * 正常运行逻辑
         * @return
         */
        T run() throws Exception;

        /**
         * 异常回调
         * @return
         */
        T fallBack();
    }
}

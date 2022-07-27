package com.yrc.juc.a_线程创建方式;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 线程创建方式3：创建 FutureTask 对象，任务具有返回值
 */
@Slf4j(topic = "c.CreateWay3")
public class CreateWay3 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建 futureTask 类对象
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            log.debug("running");
            Thread.sleep(1000);
            return 100;
        });

        // 创建线程
        Thread t1 = new Thread(futureTask, "t1");
        // 启动任务
        t1.start();
        Integer result = futureTask.get();  // 阻塞执行
        log.debug("result = {}", result);

        log.debug("running"); // 打印完 result 后才会执行本行代码
    }
}

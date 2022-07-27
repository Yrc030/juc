package com.yrc.juc.b_Thread类常用API测试;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 测试调用 start 和 run 方法的区别
 */
@Slf4j(topic = "c.TestStartAndRun")
public class TestStartAndRun {

    /**
     * 用于测试: Thread#Run()
     */
    @Test
    public void testRun() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t1.run(); // 同步调用，仍然是 main 线程调用的 run 方法
        log.debug("running...");
    }


    /**
     * 用于测试: Thread#start()
     */
    @Test
    public void testStart() {
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t2.start(); // 异步调用，由t2线程调用run方法
        log.debug("running...");
    }
}

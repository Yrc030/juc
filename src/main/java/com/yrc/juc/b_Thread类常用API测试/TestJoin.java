package com.yrc.juc.b_Thread类常用API测试;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * User: joker
 * Date: 2022-07-25-16:38
 * Time: 16:38
 */
@Slf4j(topic = "c.TestJoin")
public class TestJoin {

    static int r = 0;

    public static void main(String[] args) throws InterruptedException {
        // method1();   // 案例1
         method2();   // 案例2
    }

    // 案例1：join 的使用
    private static void method1() throws InterruptedException{

        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("开始");
            sleep(1);
            r = 10;
            log.debug("结束");
        }, "t1");
        t1.start();
        // t1.join();  // 使用join与不使用join，r的结果不同。使用join时，主线程会等待直到t1线程结束才执行下面的代码。
        log.debug("r = {}", r);
        log.debug("结束");
    }

    // 案例2：多个线程并行执行的总耗时
    private static void method2() throws InterruptedException{
        Thread t1 = new Thread(() -> {
            sleep(1);
            r = 10;
        }, "t1");
        Thread t2 = new Thread(() -> {
            sleep(2);
            r = 20;
        }, "t1");
        t1.start();
        t2.start();
        log.debug("join begin...");
        long begin = System.currentTimeMillis();
        t1.join();
        log.debug("t1 join end");
        t2.join();
        log.debug("t2 join end");
        long end = System.currentTimeMillis();
        log.debug("r = {}", r);
        log.debug("used time = {}ms", (end - begin));  // 2000ms
    }



    private static void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

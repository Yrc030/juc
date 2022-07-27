package com.yrc.juc.d_Java线程的6种状态;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 模拟泡茶，情况是：开水没有；水壶要洗，茶壶，茶杯要洗；火已生了，茶叶也有了。怎么办？
 * <p>
 * Thread1: -- 洗水壶(1s) --> ---------------- 烧水(15s) -----------------------------> 泡茶(16s)
 * *                      ↘                             ↗
 * *                        ↘                          ↗
 * *                          ↘                       ↗
 * *                  Thread2: 洗茶壶，洗茶杯，放茶叶(4s)
 */
@Slf4j(topic = "c.MakeTea")
public class MakeTea {


    public static void main(String[] args) throws InterruptedException {

        Thread t1 = Thread.currentThread();
        t1.setName("t1"); // 主线程为 t1

        long start = System.currentTimeMillis();
        log.debug("洗水壶...");
        TimeUnit.SECONDS.sleep(1);
        log.debug("烧开水...");
        Thread t2 = new Thread(() -> {
            try {
                log.debug("洗茶壶...");
                TimeUnit.SECONDS.sleep(1);
                log.debug("洗茶杯...");
                TimeUnit.SECONDS.sleep(2);
                log.debug("放茶叶...");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");
        t2.start();
        TimeUnit.SECONDS.sleep(15);
        log.debug("水烧开了!");
        log.debug("泡茶!");
        long end = System.currentTimeMillis();
        log.debug("泡茶花费时间: {}", (end - start));

    }

}

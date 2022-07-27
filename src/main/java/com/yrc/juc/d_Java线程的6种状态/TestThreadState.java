package com.yrc.juc.d_Java线程的6种状态;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * User: joker
 * Date: 2022-07-26-21:25
 * Time: 21:25
 */
@Slf4j(topic = "c.TestThreadState")
public class TestThreadState {

    public static void main(String[] args) throws InterruptedException {

        // NEW
        Thread t1 = new Thread(() -> {
            log.debug("running...");
        }, "t1");

        // RUNNABLE: 包括操作系统层面的：就绪、运行、阻塞三种状态
        Thread t2 = new Thread(() -> {
           synchronized (TestThreadState.class) {
               while (true) {
                    if(Thread.currentThread().isInterrupted()) {
                        break;
                    }
               }
           }
        }, "t2");
        t2.start();

        // Blocked
        Thread t3 = new Thread(() -> {
            // t2 线程拿到了锁，所以 t3 线程会处于 Blocked 状态
            synchronized (TestThreadState.class) {
                log.debug("running...");
            }
        }, "t3");
        t3.start();

        // Waiting
        Thread t4 = new Thread(() -> {
            try {
                // 只有 t3 线程执行完毕，t4 线程才会继续执行，t4处于 Waiting 状态
                t3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t4");
        t4.start();

        // Timed Waiting
        Thread t5 = new Thread(() -> {
            try {
                // t5 处于 Timed Waiting  状态
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t5");
        t5.start();


        // TERMINATED
        Thread t6 = new Thread(() -> {
            log.debug("running...");
        }, "t6");
        t6.start();

        // 输出各线程的状态
        log.debug("t1: {}", t1.getState());
        log.debug("t2: {}", t2.getState());
        log.debug("t3: {}", t3.getState());
        log.debug("t4: {}", t4.getState());
        log.debug("t5: {}", t5.getState());
        log.debug("t6: {}", t6.getState());

        //TimeUnit.SECONDS.sleep(2);
        //t2.interrupt();  打断 t2 线程后， t2,t3,t4 线程都会转为 TERMINATED 状态
        //log.debug("t1: {}", t1.getState());
        //log.debug("t2: {}", t2.getState());
        //log.debug("t3: {}", t3.getState());
        //log.debug("t4: {}", t4.getState());
        //log.debug("t5: {}", t5.getState());
        //log.debug("t6: {}", t6.getState());


    }
}

package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * User: joker
 * Date: 2022-07-31-17:23
 * Time: 17:23
 */
@Slf4j(topic = "c.TestWaitAndNotify")
public class TestWaitAndNotify {

    public static final Object LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        //method1(); // 案例1
        //method2(); // 案例2
        method3(); // 案例3
    }

    /**
     * 案例1：线程获取了对象锁才能调用，调用 wait() / notify() / notifyAll()
     */
    private static void method1() throws InterruptedException {
        synchronized (LOCK) {
            log.debug("wait...");
            LOCK.wait();
        }
        //如果为获取对象锁直接调用，都会抛出异常 IllegalMonitorStateException
        // LOCK.wait();
        // LOCK.notify();
        // LOCK.notifyAll();

    }

    /**
     * 案例2：notify() 和 notifyAll() 的区别
     */
    private static void method2() throws InterruptedException {

        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("wait...");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("wake up");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (LOCK) {
                log.debug("wait...");
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("wake up");
            }
        }, "t2").start();

        TimeUnit.SECONDS.sleep(2);
        synchronized (LOCK) {
            //log.debug("Main thread wakes up a thread in wait set randomly");
            //LOCK.notify();
            log.debug("Main thread wakes up all threads  in wait set");
            LOCK.notifyAll();
        }

    }

    /**
     * 案例3：wait() 和 wait(long)
     */
    private static void method3() throws InterruptedException {

        synchronized (LOCK) {
            log.debug("wait...");
            //LOCK.wait(); // 等待，直到被唤醒
            LOCK.wait(2000); // 等待，直接被唤醒或2s后退出等待。
            log.debug("wake up");
        }

    }
}

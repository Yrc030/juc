package com.yrc.juc.b_Thread类常用API测试;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * User: joker
 * Date: 2022-07-24-18:04
 * Time: 18:04
 */
@Slf4j(topic = "c.TestSleep")
public class TestSleep {

    /**
     * 测试 sleep 前后线程状态的变化
     */
    @Test
    public static void test1(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("enter sleep...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.debug("{} interrupted",Thread.currentThread().getName());
                }
            }
        };
        log.debug("t1 status: {}", t1.getState());    // t1 status: NEW
        t1.start();
        log.debug("t1 status: {}", t1.getState());    // t1 status: RUNNABLE
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.debug("{} interrupted",Thread.currentThread().getName());
        }
        log.debug("t1 status: {}", t1.getState());   // t1 status: TIMED_WAITING
    }


    /**
     * 用于测试: interrupt
     */
    @Test
    public void test2(){
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("enter sleep...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.debug("{} interrupted",Thread.currentThread().getName());
                }
            }
        };

        t1.start();
        t1.interrupt();  // 打断t1线程
    }

    /**
     * 用于测试: TimeUnit#sleep()
     */
    @SneakyThrows
    @Test
    public void test3() {
        Thread t3 = new Thread("t3") {
            @SneakyThrows
            @Override
            public void run() {
                log.debug("enter sleep...");
                TimeUnit.SECONDS.sleep(1);  // 睡眠 1s
                log.debug("wake up");
            }
        };
        t3.start();
        t3.join();  // 不加 join，则 main 线程执行完毕后直接退出，不会输出 wake up
    }
}

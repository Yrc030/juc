package com.yrc.juc.c_Java守护线程;

import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 * User: joker
 * Date: 2022-07-26-17:59
 * Time: 17:59
 */
@Slf4j(topic = "c.TestDaemonThread")
public class TestDaemonThread {
    // 只有当所有「非守护线程」执行完毕后，java进程才会结束。
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while (true) {
                Thread ct = Thread.currentThread();
                if (ct.isInterrupted()) {
                    break;
                }
            }
            log.debug("end");
        }, "t1");
        t1.setDaemon(true); // 设置 t1 为守护线程
        t1.start();
        log.debug("end");
    }
}

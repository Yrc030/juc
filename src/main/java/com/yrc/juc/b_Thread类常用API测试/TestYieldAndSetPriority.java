package com.yrc.juc.b_Thread类常用API测试;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j(topic = "c.TestYieldAndSetPriority")
public class TestYieldAndSetPriority {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            int count = 0;
            for (; ; ) {

                log.debug("----> count = {} ", (count++));
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            int count = 0;
            for (; ; ) {
                //Thread.yield();  打开注释测试
                log.debug("     ----> count = {} ", (count++));
            }
        }, "t2");
        //t1.setPriority(Thread.MIN_PRIORITY);
        //t2.setPriority(Thread.MAX_PRIORITY);
        t1.start();
        t2.start();
    }
}

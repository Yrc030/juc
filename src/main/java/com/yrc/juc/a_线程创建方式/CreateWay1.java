package com.yrc.juc.a_线程创建方式;

import lombok.extern.slf4j.Slf4j;


/**
 * 线程创建方式1：实现 Thread#run() 方法
 */
@Slf4j(topic = "c.CreateWay1")
public class CreateWay1 {
    public static void main(String[] args) {
        // 创建线程对象
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("runing");
            }
        };
        // 启动线程
        t1.start();

        log.debug("runing"); // 主线程
    }
}

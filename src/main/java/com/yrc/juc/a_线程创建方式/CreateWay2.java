package com.yrc.juc.a_线程创建方式;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程创建方式2：实现 Runnable 接口
 */
@Slf4j(topic = "c.CreateWay2")
public class CreateWay2 {
    public static void main(String[] args) {
        // 创建线程
        Thread t1 = new Thread(() -> log.debug("running") , "t1");
        // 启动线程
        t1.start();
        log.debug("running");
    }
}

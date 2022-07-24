package com.yrc.juc.example1;

import com.yrc.juc.example1.util.FileReader;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 * User: joker
 * Date: 2022-07-24-9:48
 * Time: 9:48
 */
@Slf4j(topic = "c.Async")
public class Async {
    public static void main(String[] args) {
        // 异步执行
        new Thread(() -> FileReader.read("src/main/java/com/yrc/juc/example1/text.txt"), "线程1").start(); // 在线程1执行的同时，主线程继续执行
        log.debug("do other things...");
    }
}

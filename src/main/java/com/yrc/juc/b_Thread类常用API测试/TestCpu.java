package com.yrc.juc.b_Thread类常用API测试;

/**
 * 防止cpu过度占用的方法
 */
public class TestCpu {
    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1);  // 死循环中加上睡眠
                } catch (Exception e) {
                }
            }
        }).start();
    }
}

package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: 共享带来的安全问题
 */
@Slf4j(topic = "c.SecurityIssues")
public class SecurityIssues {

    // 临界资源
    private static int i = 0;

    /**
     * 两个线程同时对静态变量 i 进行自增和自减 5000 次，结果是否为 0 ?
     * 不一定，因为 i++ 和 i-- 并非原子性的操作，以 i++ 为例，其JVM字节码指令如下:
     * ```
     * getstatic i // 获取静态变量i的值
     * iconst_1 // 准备常量1
     * iadd // 自增
     * putstatic i // 将修改后的值存入静态变量i
     * ```
     * 在 t1 线程进行 i++ 操作但未将自增的值写回给 i 时发生上下文切换，t2 线程进行 i-- 操作并将自减的值写回给 i，接着 t1 线程回到
     * 运行状态会将之前保存的自增值直接写回给 i，此时就出现了线程安全的问题。
     * <p>
     * 解决方式：
     * - 阻塞式的解决方案：synchronized，lock
     * - 非阻塞式的解决方案：原子变量
     */
    public static void main(String[] args) throws InterruptedException {
        // method1(); // 案例1
        //  method2(); // 案例2
        method3(); // 案例3
    }


    // 案例1：共享资源产生的线程安全问题
    private static void method1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // 临界区
                i++;
            }
        }, "t1");


        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                // 临界区
                i--;
            }
        }, "t2");


        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.debug("i = {}", i);
    }


    // 案例2：使用 synchronized 同步代码块解决线程安全问题。
    private static void method2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                synchronized (SecurityIssues.class) {
                    // 临界区
                    i++;
                }
            }

        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                synchronized (SecurityIssues.class) {
                    // 临界区
                    i--;
                }
            }
        }, "t2");


        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.debug("i = {}", i);
    }

    // 案例3: 面向对象思想进行改进
    public static void method3() throws InterruptedException {
        Counter Counter = new Counter();

        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                synchronized (SecurityIssues.class) {
                    // 临界区
                    Counter.increment();
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                synchronized (SecurityIssues.class) {
                    // 临界区
                    Counter.decrement();
                }
            }
        }, "t2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.debug("i = {}", Counter.getI());
    }
}

class Counter {
    private int i;

    public synchronized void increment() {
        i++;
    }
    public synchronized void decrement() {
        i--;
    }
    public synchronized int getI() {
        return i;
    }
}

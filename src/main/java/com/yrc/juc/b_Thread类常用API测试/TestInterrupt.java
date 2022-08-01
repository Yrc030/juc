package com.yrc.juc.b_Thread类常用API测试;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Description:
 * User: joker
 * Date: 2022-07-25-17:17
 * Time: 17:17
 */
@Slf4j(topic = "c.TestInterrupt")
public class TestInterrupt {

    public static void main(String[] args) throws InterruptedException {
        // method1();  // 案例1
        method2();  // 案例2
        //method3();
    }

    // 案例1：sleep，wait, join 被 interrupt 打断时，会将打断标记重置为 false
    private static void method1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                log.debug("sleep...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // sleep，wait, join 被打断后会在异常处理期间将打断标记置位 false;
                e.printStackTrace();
            }
        }, "t1");

        t1.start();
        Thread.sleep(1000);
        log.debug("interrupting...");
        t1.interrupt();
        // Thread.sleep(1000);
        // 如果输出在异常处理之前，则打断标记为true，如果输出在异常处理之后，则打断标记为false，可以通过打开上一行代码进行测试
        log.debug("flag of interrupted sleep: {}", t1.isInterrupted());
    }

    // 案例2：正常运行的代码被打断时，打断标记为 true，不会重置
    private static void method2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (; ; ) {
                Thread ct = Thread.currentThread();
                if (ct.isInterrupted()) {  // isInterrupted 调用后，不会重置打断标记
                    log.debug("interrupted: {}", ct.isInterrupted());
                    break;
                }
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
    }

    // 案例3：Thread.interrupted() 和 LockSupport.park()
    // LockSupport.park() 与 Thread#sleep() 方法类似，使当前线程进入休眠，除非打断标记为true。
    // Thread.interrupted() 会返回当前线程的打断标记，并重写置为 false。
    // 上面两个方法配合使用：
    private static void method3() throws InterruptedException {
        Thread t1 = new Thread(() -> {

            log.debug("park1...");
            LockSupport.park();
            log.debug("unpark1...");
            Thread ct = Thread.currentThread();
            log.debug("打断标记: {}", ct.isInterrupted());  // true，不重置为 false，后面再调用 park 则不会生效
            //log.debug("打断标记: {}", Thread.interrupted()); // true，重置为 false，后面再调用 park 依然生效

            log.debug("park2...");
            LockSupport.park();
            log.debug("unpark2...");

        }, "t1");


        t1.start();
        TimeUnit.SECONDS.sleep(1);
        t1.interrupt();
    }
}

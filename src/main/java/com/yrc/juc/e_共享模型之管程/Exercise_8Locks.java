package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 线程八锁，考察 synchronized 锁住的是哪个对象
 */
public class Exercise_8Locks {


    public static void main(String[] args) {
        //method1();
        //method2();
        //method3();
        //method4();
        //method5();
        //method6();
        //method7();
        method8();
    }


    /**
     * 输出结果？  1 2 或者 2 1，取决于哪个线程先抢到锁对象
     */
    public static void method1() {
        Lock1 lock1 = new Lock1();
        new Thread(lock1::a, "t1").start();
        new Thread(lock1::b, "t2").start();
    }

    /**
     * 输出结果？ 1s 1 2 或者 2 1s 1，取决于哪个线程先抢到锁对象
     */
    public static void method2() {
        Lock2 lock2 = new Lock2();
        new Thread(lock2::a, "t1").start();
        new Thread(lock2::b, "t2").start();
    }

    /**
     * 输出结果？
     * t1 先抢到锁： 3 1s 1 2
     * t2 先抢到锁： t2 先获取时间片： 2 3 1s 1
     * *            t3 先获取时间片： 3 2 1s 1
     */
    private static void method3() {
        Lock3 lock3 = new Lock3();
        new Thread(lock3::a, "t1").start();
        new Thread(lock3::b, "t2").start();
        new Thread(lock3::c, "t3").start();
    }

    /**
     * 输出结果？ 2 1s 1，t1 和 t2 获取的锁对象不同
     */
    private static void method4() {
        Lock4 lock4_a = new Lock4();
        Lock4 lock4_b = new Lock4();
        // 锁对象不同
        new Thread(lock4_a::a, "t1").start();
        new Thread(lock4_b::b, "t2").start();
    }

    /**
     * 输出结果？ 2 1s 1，t1 和 t2 获取的锁对象不同
     */
    private static void method5() {
        Lock5 lock5 = new Lock5();
        // 锁对象不同
        new Thread(() -> lock5.a(), "t1").start();
        new Thread(() -> lock5.b(), "t2").start();
    }

    /**
     * 输出结果？ 1s 1 2 或者 2 1s 1，取决于哪个线程先抢到锁对象
     */
    private static void method6() {
        Lock6 lock6 = new Lock6();
        // 锁对象相同
        new Thread(() -> lock6.a(), "t1").start();
        new Thread(() -> lock6.b(), "t2").start();
    }

    /**
     * 输出结果？ 2 1s 1,  t1 和 t2 获取的锁对象不同
     */
    private static void method7() {
        Lock7 lock7_a = new Lock7();
        Lock7 lock7_b = new Lock7();
        // 锁对象不同
        new Thread(() -> lock7_a.a(), "t1").start();
        new Thread(() -> lock7_b.b(), "t2").start();
    }

    /**
     * 输出结果？ 1s 1 2 或者 2 1s 1，取决于哪个线程先抢到锁对象
     */
    private static void method8() {
        Lock8 lock8_a = new Lock8();
        Lock8 lock8_b = new Lock8();
        // 锁对象相同
        new Thread(() -> lock8_a.a(), "t1").start();
        new Thread(() -> lock8_b.b(), "t2").start();
    }


}

@Slf4j(topic = "c.Lock1")
class Lock1 {
    // 锁对象: this
    public synchronized void a() {
        log.debug("1");
    }

    public synchronized void b() {
        log.debug("2");
    }
}

@Slf4j(topic = "c.Lock2")
class Lock2 {
    // 锁对象: this
    public synchronized void a() {
        try {
            SECONDS.sleep(1);
            log.debug("1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void b() {
        log.debug("2");
    }
}

@Slf4j(topic = "c.Lock3")
class Lock3 {
    // 锁对象: this
    public synchronized void a() {
        try {
            SECONDS.sleep(1);
            log.debug("1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void b() {
        log.debug("2");
    }

    public void c() {
        log.debug("3");
    }
}

@Slf4j(topic = "c.Lock4")
class Lock4 {
    // 锁对象: this
    public synchronized void a() {
        try {
            SECONDS.sleep(1);
            log.debug("1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public synchronized void b() {
        log.debug("2");
    }
}

@Slf4j(topic = "c.Lock5")
class Lock5 {
    // 锁对象: Lock5.class
    public static synchronized void a() {
        try {
            SECONDS.sleep(1);
            log.debug("1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // 锁对象: this
    public synchronized void b() {
        log.debug("2");
    }
}


@Slf4j(topic = "c.Lock6")
class Lock6 {
    // 锁对象: Lock6.class
    public static synchronized void a() {
        try {
            SECONDS.sleep(1);
            log.debug("1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void b() {
        log.debug("2");
    }
}


@Slf4j(topic = "c.Lock7")
class Lock7 {
    // 锁对象: Lock7.class
    public static synchronized void a() {
        try {
            SECONDS.sleep(1);
            log.debug("1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // 锁对象: this
    public synchronized void b() {
        log.debug("2");
    }
}


@Slf4j(topic = "c.Lock8")
class Lock8 {
    // 锁对象: Lock8.class
    public static synchronized void a() {
        try {
            SECONDS.sleep(1);
            log.debug("1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void b() {
        log.debug("2");
    }
}


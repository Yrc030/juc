package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.List;
import java.util.Vector;

/**
 * 使用 jol 输出对象头的 Mark Word 来观察偏向锁的状态
 */
@Slf4j(topic = "c.LockPrinciple_BiasedLock")
public class LockPrinciple_BiasedLock {

    public static void main(String[] args) throws InterruptedException {
        //method1(); // 案例1
        //method2(); // 案例2
        //method3(); // 案例3
        //method4(); // 案例4
        method5();
    }


    /**
     * 案例一：偏向锁的开启
     * 对象默认开始偏向锁，并且偏向锁默认是延迟4s生效的，添加 VM 参数 -XX:BiasedLockingStartupDelay=0 可以禁用延迟
     */
    public static void method1() {
        Object obj = new Object();
        // TimeUnit.SECONDS.sleep(4);
        log.debug("加锁前: \n{}", markWord2Binary(ClassLayout.parseInstance(obj).toPrintable()));
        synchronized (obj) {
            log.debug("加锁中: \n{}", markWord2Binary(ClassLayout.parseInstance(obj).toPrintable()));
        }
        // 处于偏向锁的对象解锁后，线程 id 仍存储于对象头中也就是偏(心)向某个线程了
        log.debug("解锁后: \n{}", markWord2Binary(ClassLayout.parseInstance(obj).toPrintable()));
    }


    /**
     * 案例二：偏向锁撤销之调用锁对象的 hashcode
     * 对象第一次调用 hashcode 时，才会将 hashcode 写入到对象的 Mark Word 中，并且调用对象的 hashcode 会禁用掉偏向锁，因为
     * hashcode 和 偏向锁的 threadId 位冲突
     */
    public static void method2() {
        // 对象默认开始偏向锁
        Object obj = new Object();
        // 在加锁前调用锁对象的 hashcode，锁对象的 Mark Word 被写入 hashcode 并且低3位修改为 001
        // obj.hashCode();
        log.debug("加锁前: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
        synchronized (obj) {
            // 在加锁中（偏向锁）调用锁对象的 hashcode， 锁被替换位重量级锁 10
            // obj.hashCode();
            log.debug("加锁中: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
        }
        // 在加锁后调用锁对象的 hashcode，锁对象的 Mark Word 被写入 hashcode 并且低3位修改为 001
        obj.hashCode();
        log.debug("解锁后: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
    }

    /**
     * 案例三：偏向锁撤销之其它线程获取锁对象
     * 当有其它线程尝试获取，并且没有与之前获取偏向锁的线程产生竞争，即交替执行，则偏向锁会被升级为轻量锁
     */
    public static void method3() throws InterruptedException {
        Object obj = new Object();

        Thread t1 = new Thread(() -> {
            log.debug("加锁前: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));  // 00..00101
            synchronized (obj) {
                log.debug("加锁中: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));  // t1线程id+101
            }
            log.debug("解锁后: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));  // t1线程id+101
        }, "t1");
        Thread t2 = new Thread(() -> {
            log.debug("加锁前: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable())); // t1线程id+101
            synchronized (obj) {
                log.debug("加锁中: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));  // t2线程锁纪录+00
            }
            log.debug("解锁后: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));  // 00..00101
        }, "t2");

        t1.start();
        t1.join();
        t2.start();
    }

    /**
     * 案例四：批量重偏向
     * 如果对象被多个线程访问，但没有竞争，这时偏向了线程 `t1` 的锁对象仍有机会重新偏向 `t2`，重偏向会重置锁对象的 `Thread ID`
     * 当撤销偏向锁阈值超过 `20` 次后，`JVM` 会觉得是不是偏向错了，于是在给这些对象加锁时重新偏向至加锁线程。
     */
    private static void method4() throws InterruptedException {

        List<Object> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Object obj = new Object();
                log.debug("{}\t加锁前: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                synchronized (obj) {
                    log.debug("{}\t加锁中: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                }
                log.debug("{}\t解锁后: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                list.add(obj);
            }

        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Object obj = list.get(i);
                log.debug("{}\t加锁前: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                synchronized (obj) {
                    log.debug("{}\t加锁中: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                }
                log.debug("{}\t解锁后: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
            }

        }, "t2");

        t1.start();
        t1.join();
        log.debug("============================================================");
        t2.start();
        t2.join();
        log.debug("====================最终所有对象的Mark Word==================");
        list.forEach(obj -> log.debug(getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable())));
    }

    /**
     * 案例五：批量撤销
     * 当撤销偏向锁阈值超过 `40` 次后，`JVM` 会觉得自己确实偏向错了，根本就不该偏向，于是整个类的所有对象都会变为不可偏向的，
     * 新建的对象也是不可偏向的。
     */
    private static void method5() throws InterruptedException {

        List<Object> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                // t1 线程的执行将使得所有锁对象都偏向t1 线程， t1TheadId+101
                Object obj = new Object();
                log.debug("{}\t加锁前: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                synchronized (obj) {
                    log.debug("{}\t加锁中: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                }
                log.debug("{}\t解锁后: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                list.add(obj);
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            // t2 线程执行会导致t1线程的偏向锁被撤销 20 次（锁升级）， t1TheadId+101 --> lockRecordLocation+00 --> 00...01
            // 20次后，后面的执行都属于t1线程的偏向锁重偏向至 t2，t1TheadId+101 --> t2TheadId+101
            for (int i = 0; i < 50; i++) {
                Object obj = list.get(i);
                log.debug("{}\t加锁前: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                synchronized (obj) {
                    log.debug("{}\t加锁中: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                }
                log.debug("{}\t解锁后: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
            }
        }, "t2");


        Thread t3 = new Thread(() -> {
            // t3 线程执行前20次，锁对象的MarkWord为 00..01，会获取轻量级锁：00...01 --> lockRecordLocation+00 -> 00...01
            // 20次后，后面的执行，锁对象都偏向 t2，偏向锁会被撤销，t2TheadId+101 --> lockRecordLocation+00 --> 00...01
            for (int i = 0; i < 40; i++) {
                Object obj = list.get(i);
                log.debug("{}\t加锁前: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                synchronized (obj) {
                    log.debug("{}\t加锁中: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
                }
                log.debug("{}\t解锁后: {}", i, getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
            }
        }, "t3");

        t1.start();
        t1.join();
        log.debug("============================================================");
        t2.start();
        t2.join();
        log.debug("============================================================");
        t3.start();
        t3.join();
        log.debug("====================最终所有对象的Mark Word==================");
        list.forEach(obj -> log.debug(getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable())));

        Object obj = new Object();
        // 偏向锁被撤销20次后，该类的所有对象（不包含以创建但撤销偏向锁的对象）都被设置为不可偏向，包括新创建的对象  001
        log.debug("新创建 obj 的 Mark Word: {}", getMarkWordBinary(ClassLayout.parseInstance(obj).toPrintable()));
    }


    // 辅助方法，请忽略
    private static String markWord2Binary(String table) {
        String markWordHex = getMarkWordHex(table);
        String markWordBinary = getMarkWordBinary(table);
        table = table.replace(markWordHex, markWordBinary);
        return table;
    }

    private static String getMarkWordHex(String table) {
        int begin = table.indexOf("0x");
        if (begin == -1) {
            return table;
        }
        int end1 = table.indexOf(" ", begin);
        int end2 = table.indexOf("\r", begin);
        int end3 = table.indexOf("\n", begin);
        int end = Integer.min(end1, Integer.min(end2, end3));

        return table.substring(begin, end);
    }

    private static String getMarkWordBinary(String table) {
        String markWordHex = getMarkWordHex(table);
        long value = Long.parseLong(markWordHex.substring(2), 16);
        String markWordBin = Long.toBinaryString(value);
        StringBuilder buffer = new StringBuilder();
        buffer.append(markWordBin);
        String property = System.getProperty("os.arch");

        int bits = property.endsWith("64") ? 64 : 32;

        // 补齐剩余位（填充 0）
        for (int i = markWordBin.length(); i < bits; i++) {
            buffer.insert(0, "0");
        }

        // 每4位填充一个空格
        int count = 0;
        for (int i = 1; i < bits; i++) {
            if ((i % 8) != 0) {
                continue;
            }
            int offset = i + count;
            if (offset >= buffer.length()) {
                break;
            }
            buffer.insert(offset, " ");
            count++;
        }

        return buffer.toString();
    }
}

package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;

import javax.management.monitor.Monitor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 局部变量线程安全分析
 */
@Slf4j(topic = "c.LocalVariableSecurityAnalysis")
public class LocalVariableSecurityAnalysis {

    public static void main(String[] args) {
        //method1(); // 案例1

        // 案例2
        //Container container = new Container();
        //Container container = new SubContainer();
        //new Thread(container::init, "t1").start();
        //new Thread(container::init, "t2").start();

        method2(); // 案例3
    }

    // 案例1: 局部变量为基本类型时，不存在线程安全问题，每个线程都会在自己的栈帧内存中为 i 创建副本
    public static void method1() {
        int i = 10;
        i++;
    }



    // 案例2: 局部变量为引用类型时
    static class Container {
        public void init() {

            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                add(list);
                sub(list);
            }
            log.debug("list = {}", list);
        }

        public void add(List<Integer> list) {
            list.add(1);
        }

        public void sub(List<Integer> list) {
            list.remove(0);
        }
    }

    static class SubContainer extends Container {
        @Override
        public void sub(List<Integer> list) {
            new Thread(() -> list.remove(0), "st").start();
        }
    }

    // 案例3: 多个原子操作组合在一起不是原子操作，存在线程安全问题
    public static void method2() {
        Hashtable<String, Object> table = new Hashtable<>();
        Thread t1 = new Thread(() -> {
            if (table.get("key") == null) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("previous value: {}", table.put("key", "value1"));
                ;
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            if (table.get("key") == null) {
                log.debug("previous value: {}", table.put("key", "value2"));
                ;
            }
        }, "t2");

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.debug("table = {}", table);
    }
}

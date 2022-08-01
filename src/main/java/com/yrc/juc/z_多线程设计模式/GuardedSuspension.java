package com.yrc.juc.z_多线程设计模式;

import com.yrc.juc.util.Downloader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 保护性暂停模型
 */
@Slf4j(topic = "c.GuardedSuspension")
public class GuardedSuspension {


    public static void main(String[] args) {
        case1(); // 案例1
    }


    /**
     * 案例1：模拟 线程t1 等待 线程t2 的下载结果
     */
    private static void case1() {
        GuardedObject<List<String>> guardedObject = new GuardedObject<>();
        new Thread(() -> {
            log.debug("wait...");
            List<String> response = guardedObject.get(3, TimeUnit.SECONDS);
            if (response == null) {
                log.debug("timeout");
            } else {
                log.debug("size: {}", response.size());
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                log.debug("downloading...");
                List<String> response = Downloader.download();
                //TimeUnit.SECONDS.sleep(3);  // 模拟等待超时
                guardedObject.set(null); // 模拟虚假唤醒
                //guardedObject.set(response);
                log.debug("complete");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }

    static class GuardedObject<R> {
        private R response;

        /**
         * 同步获取结果，无结果则进入 WAITING，直到获取结果或超时退出
         *
         * @param timeout  等待时间
         * @param timeUnit 时间单位
         * @return 结果
         */
        public synchronized R get(long timeout, TimeUnit timeUnit) {
            // 等待时间
            long millisTimeout = timeUnit.toMillis(timeout);
            // 开始时间
            long beginTime = System.currentTimeMillis();
            // 经过时间
            long passedTime = 0;
            while (this.response == null) {
                // 每次循环的等待时间为剩余时间
                long waitTime = millisTimeout - passedTime;
                // 等待时间 <= 0 退出循环
                if (waitTime <= 0) {
                    break;
                }
                try {
                    log.debug("get...");
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 求得经过时间
                passedTime = System.currentTimeMillis() - beginTime;
            }
            return this.response;
        }

        /**
         * 同步设置结果，并唤醒等待线程
         */
        public synchronized void set(R response) {
            this.response = response;
            // 唤醒所有等待线程
            this.notifyAll();
        }
    }
}

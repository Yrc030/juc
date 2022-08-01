package com.yrc.juc.z_多线程设计模式;

import lombok.extern.slf4j.Slf4j;

/**
 * 两阶段终止模式
 */
@Slf4j(topic = "c.TwoStageTermination")
public class TwoPhaseTermination {

    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination thread = new TwoPhaseTermination();
        thread.start();
        Thread.sleep(3500);
        thread.stop();
    }

    private Thread thread;

    // 开启线程
    public void start() {
        thread = new Thread(() -> {
            while (true) {
                Thread ct = Thread.currentThread();
                if (ct.isInterrupted()) {
                    log.debug("{} has been interrupted and processing some work", ct.getName());
                    // some work
                    return;
                }
                try {
                    Thread.sleep(1000);  // sleep 被打断后会抛出异常，并将打断标记会置为 false
                    log.debug("执行代码..."); // 如果正常执行代码时被打断，则下次循环会退出。
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 如果发生异常，则需要重新设置打断标记
                    ct.interrupt();
                }
            }
        });
        thread.start();
    }


    // 停止线程
    public void stop() {
        thread.interrupt();
    }

}

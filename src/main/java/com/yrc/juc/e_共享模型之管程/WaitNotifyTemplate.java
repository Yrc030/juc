package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * wait-notify 代码模板
 */
@Slf4j(topic = "c.WaitNotifyTemplate")
public class WaitNotifyTemplate {


    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;


    public static void main(String[] args) throws InterruptedException {
        //step1(); // 阶段1
        //step2(); // 阶段2
        //step3(); // 阶段3
        //step4(); // 阶段4
        step5(); // 阶段5 final 版
    }


    /**
     * 标准模板
     */
    private boolean flag = false;
    private final Object lock = new Object();
    public void template() {
        new Thread(() -> {
            synchronized (lock) {
                while (!flag) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // work...
                }
            }
        }).start();

        new Thread(() -> {
            // 另一个线程唤醒所有等待线程
            synchronized (lock) {
                lock.notifyAll();
            }
        }).start();
    }


    /**
     * 观察下面代码存在什么问题？
     * 执行方式：使用 sleep 休眠等待
     * 问题1：使用 sleep 锁不会释放，导致其它线程不能继续执行，代码执行效率低。
     * 问题2：即使烟已经送到，小南也必须等到 2s 后才能继续执行。
     * 解决方式；wait-notify
     */
    private static void step1() throws InterruptedException {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            // 这里能不能加 synchronized (room)？ 不能，因为加了锁之后，在小南释放锁之前，该线程都无法执行，也就没办法给小南送烟
            hasCigarette = true;
            log.debug("烟到了噢！");
        }, "送烟的").start();
    }


    /**
     * 观察下面代码存在什么问题？
     * 执行方式：使用 wait-notify 机制进行休眠等待
     * 问题1：如果有多个线程都在等待，notify 只能随机唤醒一个线程。
     */
    private static void step2() throws InterruptedException {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            // 这里必须加 room 锁，否则无法调用 notify 唤醒小南
            synchronized (room) {
                hasCigarette = true;
                log.debug("烟到了噢！");
                room.notify();
            }
        }, "送烟的").start();
    }


    /**
     * 观察下面代码存在什么问题？
     * 执行方式：存在多个线程进行等待。
     * 问题1: notify 会随机唤醒一个线程，如下面示例，小女的外卖到了可能唤醒的是小南，那么小南因为没有香烟无法工作而退出，
     * 小女则因为没被唤醒则一直等待，这种情况被称作「虚假唤醒」
     * 解决方式：使用 notifyAll
     */
    private static void step3() throws InterruptedException {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                Thread thread = Thread.currentThread();
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                room.notify();
            }
        }, "送外卖的").start();
    }


    /**
     * 观察下面代码存在什么问题？
     * 执行方式：使用 notifyAll 唤醒所有等待线程
     * 问题1：使用 if + wait 的方式只有一次判断，所有的线程被唤醒后，不满足条件的线程不会执行工作代码，而是直接退出。
     * 解决方式：使用 while + wait 方式
     */
    private static void step4() throws InterruptedException {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                Thread thread = Thread.currentThread();
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                room.notifyAll();
            }
        }, "送外卖的").start();
    }


    /**
     * 正确解决方案
     * 执行方式：使用 while + wait + notifyAll 来实现
     */
    private static void step5() throws InterruptedException {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                Thread thread = Thread.currentThread();
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasCigarette) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "小女").start();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                room.notifyAll();
            }
        }, "送外卖的").start();
    }
}

package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 转账
 */
@Slf4j(topic = "c.Exercise_Transfer")
public class Exercise_Transfer {


    private static Random rd = new Random();

    // 随机 1~100
    private static int randomAmount() {
        return rd.nextInt(100) + 1;
    }

    public static void main(String[] args) {
        Account a = new Account(1000);
        Account b = new Account(1000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                a.transfer(b, randomAmount());
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 2000; i++) {
                b.transfer(a, randomAmount());

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

        log.debug("balance of a: {}", a.getBalance());
        log.debug("balance of b: {}", b.getBalance());
        log.debug("total money: {}", a.getBalance() + b.getBalance());
    }
}

class Account {
    private int balance;

    public Account(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void transfer(Account target, int amount) {
        // 临界区
        // 方法设置多个实例调用，this 锁不住其它线程中持有不同锁的对象，因此使用类锁
        synchronized (Account.class) {
            if (balance >= amount) {
                balance -= amount;
                target.setBalance(target.getBalance() + amount);
            }
        }
    }
}

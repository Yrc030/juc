package com.yrc.juc.e_共享模型之管程;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * 卖票
 */
@Slf4j(topic = "c.Exercise_SellTickets")
public class Exercise_SellTickets {

    private static Random rd = new Random();

    // 随机 1~5
    private static int randomAmount() {
        return rd.nextInt(5) + 1;
    }

    public static void main(String[] args) {
        Tickets tickets = new Tickets(10000);
        List<Thread> threadList = new ArrayList<>();
        List<Integer> amountList = new Vector<>();
        for (int i = 0; i < 2000; i++) {
            Thread t = new Thread(() -> {
                int amount = tickets.sell(randomAmount());
                amountList.add(amount);
            });
            t.start();
            threadList.add(t);
        }

        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        int count = tickets.getCount();
        log.debug("剩余票数: {}", count);
        int sum = amountList.stream().mapToInt(a -> a).sum();
        log.debug("出售票数: {}", sum);
        log.debug("总票数: {}", sum + count);

    }

    static class Tickets {
        private int count;

        public Tickets(int count) {
            this.count = count;
        }

        public synchronized int sell(int amount) {
            // 临界区，临界资源 count
            if (this.count >= amount) {
                try {
                    Thread.sleep(5);  // 模拟并发
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.count -= amount;
                return amount;
            }
            return 0;
        }

        public int getCount() {
            return this.count;
        }
    }

}

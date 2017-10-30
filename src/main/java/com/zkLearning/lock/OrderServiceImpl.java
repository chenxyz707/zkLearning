package com.zkLearning.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**
 * 测试分布式锁的service
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-30
 */
public class OrderServiceImpl implements Runnable {

    private static OrderCodeGenerator ocg = new OrderCodeGenerator();

    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    //同时并发的线程数
    //单台zookeeper的连接数默认为maxClientCnxns=60
    private static final int NUM = 50;

    private static CountDownLatch cdl = new CountDownLatch(NUM);

    //会发生惊群效应 当并发数过大时 会卡死
    //private Lock lock = new DistributeLock();

    private Lock lock = new ImprovedLock();

    public void createOrder() {
        String orderCode = null;

        lock.lock();
        try {
            orderCode = ocg.getOrderCode();
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }

        //logger.info("获取到的订单号id：=======================>" + orderCode);
        System.out.println("获取到的订单号id：=======================>" + orderCode);
    }

    @Override
    public void run() {
        try {
            cdl.await();
        } catch (InterruptedException e) {

        }

        //创建订单
        createOrder();
    }

    public static void main(String[] args) {
        for (int i=0; i < NUM; i++) {
            //logger.info("111");
            System.out.println("i is : " + i);
            new Thread(new OrderServiceImpl()).start();
            cdl.countDown();
        }
    }
}

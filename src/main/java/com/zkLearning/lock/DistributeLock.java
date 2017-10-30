package com.zkLearning.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁，当锁释放时，其它进程都尝试获得锁，可能会发生惊群效应
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-30
 */
public class DistributeLock implements Lock {

    private static Logger logger = LoggerFactory.getLogger(DistributeLock.class);

    private static final String ZK_IP_PORT = "127.0.0.1:2181";
    private static final String LOCK_NODE = "/lock";

    private ZkClient client = new ZkClient(ZK_IP_PORT);

    private CountDownLatch cdl = null;

    // 实现阻塞式的枷锁
    @Override
    public void lock() {
        if (tryLock()) {
            System.out.println("get lock success");
            return;
        }
        waitForLock();
        lock();
    }

    private void waitForLock() {
        IZkDataListener listener = new IZkDataListener() {

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                logger.info("------------get data delete event---------");
                if (cdl != null) {
                    cdl.countDown();
                }
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {

            }
        };

        client.subscribeDataChanges(LOCK_NODE, listener);

        if (client.exists(LOCK_NODE)) {
            try {
                cdl = new CountDownLatch(1);
                cdl.await();
            } catch (InterruptedException e) {
                logger.error("error！！", e);
            }
        }

        client.unsubscribeDataChanges(LOCK_NODE, listener);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        try {
            client.createPersistent(LOCK_NODE);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        client.delete(LOCK_NODE);
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

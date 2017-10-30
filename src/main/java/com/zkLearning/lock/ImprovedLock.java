package com.zkLearning.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 改进版的分布式锁，避免发生惊群效应
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-30
 */
public class ImprovedLock implements Lock{

    private static Logger logger = LoggerFactory.getLogger(ImprovedLock.class);

    private static final String ZK_IP_PORT = "127.0.0.1:2181";
    private static final String LOCK_NODE = "/lock";

    private ZkClient client = new ZkClient(ZK_IP_PORT, 10000, 10000, new SerializableSerializer());

    private CountDownLatch cdl = null;

    private String beforePath; // 当前请求的前一个节点

    private String currentPath; // 当前请求的节点

    public ImprovedLock() {
        if (!this.client.exists(LOCK_NODE)) {
            this.client.createPersistent(LOCK_NODE);
        }
    }

    @Override
    public void lock() {
        if(!tryLock()) {
            waitForLock();
            lock();
        } else {
            logger.info(Thread.currentThread().getName() + " 获得分布式锁！");
        }
    }

    private void waitForLock() {
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                logger.info(Thread.currentThread().getName() + ":捕获到DataDelete事件！---------------------------");
                if (cdl != null) {
                    cdl.countDown();
                }
            }
        };

        // 给排在前面的节点增加数据删除的watcher
        this.client.subscribeDataChanges(beforePath, listener);

        if (this.client.exists(beforePath)) {
            cdl = new CountDownLatch(1);

            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.client.unsubscribeDataChanges(beforePath, listener);
    }

    @Override
    public boolean tryLock() {
        // 如果currentPath为空则第一次尝试加锁，第一次加锁赋值currentPath
        if (currentPath == null || currentPath.length() <= 0) {
            // 创建一个临时顺序节点
            currentPath = this.client.createEphemeralSequential(LOCK_NODE + "/", "lock");
            System.out.println("current path is : >>>>" + currentPath);
        }

        //获取所有临时节点并排序
        List<String> childrens = this.client.getChildren(LOCK_NODE);
        Collections.sort(childrens);
        if ((LOCK_NODE + "/" + childrens.get(0)).equals(currentPath)) {
            // 如果当前节点在所有节点中排名第一则获取锁成功
            return true;
        } else {
            // 如果当期节点在所有节点排名中不是第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens, currentPath.substring(6));
            beforePath = LOCK_NODE + "/" + childrens.get(wz - 1);
        }
        return false;
    }

    @Override
    public void unlock() {
        // 删除当前临时节点
        client.delete(currentPath);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}

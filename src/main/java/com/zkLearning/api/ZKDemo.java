package com.zkLearning.api;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 连接zk并监听事件
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-18
 */
public class ZKDemo implements Watcher {

    private static final CountDownLatch cdl = new CountDownLatch(1);

    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event:" + event);
        if (KeeperState.SyncConnected == event.getState()) {
            cdl.countDown();
        }
    }

    public static void main(String[] args) throws IOException {
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new ZKDemo());
        System.out.println(zk.getState());

        try {
            cdl.await();
        } catch(Exception e) {
            System.out.println("ZK Session established.");
        }
    }
}

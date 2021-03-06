package com.zkLearning.api;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 异步调用并完成回调
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-18
 */
class ChildrenCallback implements AsyncCallback.Children2Callback {
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        System.out.println(
                "Child: " + rc + ", path: " + path + ", ctx: " + ctx + ", children: " + children + ", stat: " + stat);
    }
}

public class ZKChildrenAsyncDemo implements Watcher {
    private static final CountDownLatch cdl = new CountDownLatch(1);
    private static ZooKeeper zk = null;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper("127.0.0.1:2181", 5000, new ZKChildrenAsyncDemo());
        cdl.await();

        zk.create("/zk-test", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        zk.create("/zk-test/c1", "456".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        zk.getChildren("/zk-test", true, new ChildrenCallback(), "ok");

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState())
            if (EventType.None == event.getType() && null == event.getPath()) {
                cdl.countDown();
            } else if (event.getType() == EventType.NodeChildrenChanged) {
                try {
                    System.out.println("Child: " + zk.getChildren(event.getPath(), true));
                } catch (Exception e) {
                }
            }
    }
}

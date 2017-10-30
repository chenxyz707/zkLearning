package com.zkLearning.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 创建节点
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-27
 */
public class CreateNode {

    public static void main(String[] args) throws Exception {

        String path = "/zk-client/c1";
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000,3)).build();

        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, "test".getBytes());
    }
}

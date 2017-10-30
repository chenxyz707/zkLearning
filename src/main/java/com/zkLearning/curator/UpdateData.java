package com.zkLearning.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * 更新数据
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-27
 */
public class UpdateData {

    public static void main(String[] args) throws Exception {

        String path = "/zk-update";

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "111".getBytes());

        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath(path);

        System.out.println("current data is : " + stat.getVersion());
        System.out.println("update data is : " + client.setData().withVersion(stat.getVersion()).forPath(path, "123".getBytes()));


        Thread.sleep(5000);
    }

}

package com.zkLearning.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 建立会话
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-27
 */
public class CreateSession {

    public static void main(String[] args) throws InterruptedException {

        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000).retryPolicy(policy).build();

        client.start();
        Thread.sleep(Integer.MAX_VALUE);

    }
}

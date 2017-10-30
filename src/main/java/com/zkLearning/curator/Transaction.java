package com.zkLearning.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorMultiTransaction;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 事务
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-30
 */
public class Transaction {

    public static void main(String[] args) throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
                .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

        client.start();

        List<CuratorOp> ops = new ArrayList<>();
        CuratorMultiTransaction transaction = client.transaction();
        ops.add(client.transactionOp().create().withMode(CreateMode.PERSISTENT).forPath("/a", "data".getBytes()));
        ops.add(client.transactionOp().setData().forPath("/another/path", "data".getBytes()));
        ops.add(client.transactionOp().delete().forPath("/yet/another/path"));
        Collection<CuratorTransactionResult> results = transaction.forOperations(ops);

        for (CuratorTransactionResult result : results) {
            System.out.println(result.getForPath() + " - " + result.getType());
        }

        System.out.println(results);
        client.close();

    }
}

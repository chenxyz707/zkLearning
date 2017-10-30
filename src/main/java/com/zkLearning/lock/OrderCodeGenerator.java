package com.zkLearning.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 生成订单号
 *
 * @author chenxyz
 * @version 1.0
 * @date 2017-10-30
 */
public class OrderCodeGenerator {

    private static int i = 0;

    public String getOrderCode() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(now) + (i++);
    }
}

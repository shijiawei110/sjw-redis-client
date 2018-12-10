package com.sjw.sjw.redis.client.protocol.encoder;

/**
 * @author shijiawei
 * @version KeyEncoder.java, v 0.1
 * @date 2018/11/20
 */
public interface KeyEncoder {

    /**
     * 解锁命令
     */
    String unlockByPw(String password);

    /**
     * 删除key
     *
     * @param key
     * @return
     */
    String del(String key);

    /**
     * 过期时间 expire
     */
    String expire(String key, int outTime);

    /**
     * scan
     */
    String keyScanEncode(int index);
}

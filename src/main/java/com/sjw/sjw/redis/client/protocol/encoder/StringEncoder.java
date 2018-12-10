package com.sjw.sjw.redis.client.protocol.encoder;

/**
 * @author shijiawei
 * @version StringEncoder.java, v 0.1
 * @date 2018/11/20
 */
public interface StringEncoder {
    /**
     * get String request
     *
     * @param key
     * @return
     */
    String stringGetEncode(String key);

    /**
     * mget String request
     */
    String stringMgetEncode(String... keys);

    /**
     * set String request
     */
    String stringSetEncode(String key, String value);

    /**
     * set ex set的同时设置秒级过期时间
     */
    String stringSetExEncode(String key, String value, long second);

    /**
     * string set nx 带上过期时间  分布式锁使用
     */
    String stringSetNxEncode(String key, String value, long second);

    /**
     * 原子计数器 incrby 不影响ttl时间
     */
    String stringIncrEncode(String key, int num);
}

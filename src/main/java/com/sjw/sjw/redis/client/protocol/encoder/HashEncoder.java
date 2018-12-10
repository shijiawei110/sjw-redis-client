package com.sjw.sjw.redis.client.protocol.encoder;

/**
 * @author shijiawei
 * @version HashEncoder.java, v 0.1
 * @date 2018/12/05
 * HASH 类型 协议编码
 */
public interface HashEncoder {
    /**
     * hset
     */
    String hsetEncode(String key, String hash, String val);

    /**
     * hget
     */
    String hgetEncode(String key, String hash);

    /**
     * hmget
     */
    String hmgetEncode(String key, String... hashs);

    /**
     * hincrby
     */
    String hincrbyEncode(String key, String hash, int val);
}

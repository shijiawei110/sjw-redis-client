package com.sjw.sjw.redis.client.client;

import com.sjw.sjw.redis.client.exception.SjwRedisClientTryException;

/**
 * @author shijiawei
 * @version SjwRedisClientPool.java, v 0.1
 * @date 2018/11/17
 * 客户端池 , 每个客户端持有一个通道实例
 */
public interface SjwRedisClientPool {
    /**
     * 关闭连接池
     */
    void closePool();

    /**
     * 连接池初始化 生成n个客户端实例
     */
    void initPool();

    /**
     * 获取客户端
     */
    SjwRedisClient getClient() throws SjwRedisClientTryException;


}

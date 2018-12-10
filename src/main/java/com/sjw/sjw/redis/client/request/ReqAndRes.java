package com.sjw.sjw.redis.client.request;

import com.sjw.sjw.redis.client.client.SjwRedisClient;
import com.sjw.sjw.redis.client.netty.RedisRespone;
import io.netty.channel.Channel;

/**
 * @author shijiawei
 * @version ReqAndRes.java, v 0.1
 * @date 2018/11/20
 */
public interface ReqAndRes {
    /**
     * aqs发送请求
     *
     * @param msg
     */
    void send(String msg, Channel channel);

    /**
     * 异步容器取出数据
     *
     * @return
     */
    RedisRespone getRes(SjwRedisClient sjwRedisClient);

    /**
     * 无锁请求和获取结果
     */
    RedisRespone sendRequestNoLock(String msg, Channel channel, SjwRedisClient sjwRedisClient);

    /**
     * 手动获取锁
     */
    void acquireLock();

    /**
     * 手动解锁
     */
    void realeaseLock();

    /**
     * 设置容器数据
     */
    void setRes(RedisRespone response);
}

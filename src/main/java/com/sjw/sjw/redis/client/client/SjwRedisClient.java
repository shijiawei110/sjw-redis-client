package com.sjw.sjw.redis.client.client;

import com.sjw.sjw.redis.client.command.HashCommand;
import com.sjw.sjw.redis.client.command.KeyCommand;
import com.sjw.sjw.redis.client.command.ListCommand;
import com.sjw.sjw.redis.client.command.StringCommand;
import com.sjw.sjw.redis.client.protocol.encoder.HashEncoder;

/**
 * @author shijiawei
 * @version SjwRedisClient.java, v 0.1
 * @date 2018/11/17
 * 客户端实例 持有一个通道实例
 */
public interface SjwRedisClient extends StringCommand, KeyCommand, ListCommand, HashCommand {

    int getClientNum();

    int getOutTimeMills();

    boolean getIsLive();

    boolean getIsUsing();

    /**
     * 设置为使用中状态
     */
    void setUsing();

    /**
     * 初始化连接客户端
     *
     * @return
     */
    boolean tryConnect(String host, int port, String password);

    /**
     * 关闭客户端 如果是使用状态则返回pool中,如果是未使用状态就关闭连接
     */
    void close();

    /**
     * 销毁实例
     */
    void destroy();
}

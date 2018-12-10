package com.sjw.sjw.redis.client.command;

import java.util.List;

/**
 * @author shijiawei
 * @version ListCommand.java, v 0.1
 * @date 2018/11/21
 * list命令请求
 */
public interface ListCommand {

    int lpush(String key, String value);

    int lpush(String key, Object value);

    String rpop(String key);

    <T> T rpop(String key, Class<T> clazz);

    int lsize(String key);

    String lindex(String key, int index);

    <T> T lindex(String key, int index, Class<T> clazz);

    String[] lrange(String key, int start, int end);

    <T> List<T> lrange(String key, int start, int end, Class<T> clazz);

}

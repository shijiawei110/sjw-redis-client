package com.sjw.sjw.redis.client.command;

import java.util.List;

/**
 * @author shijiawei
 * @version HashCommand.java, v 0.1
 * @date 2018/12/05
 * hash命令请求
 */
public interface HashCommand {

    int hset(String key, String hash, String val);

    int hset(String key, String hash, Object val);

    String hget(String key, String hash);

    <T> T hget(String key, String hash, Class<T> clazz);

    List<String> hmget(String key, String... hashs);

    <T> List<T> hmget(String key, Class<T> clazz, String... hashs);

    int hincrby(String key, String hash, int num);
}

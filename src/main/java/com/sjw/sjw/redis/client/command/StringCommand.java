package com.sjw.sjw.redis.client.command;

/**
 * @author shijiawei
 * @version StringCommand.java, v 0.1
 * @date 2018/11/21
 * string命令请求
 */
public interface StringCommand {
    /**
     * String 类型 get
     */
    String stringGet(String key);

    /**
     * String 类型 mget
     */
    String[] stringMget(String... keys);

    /**
     * String 类型 set
     */
    String stringSet(String key,String value);

    /**
     * String set ex
     */
    String stringSet(String key, String value, long second);
    /**
     * String set nx带过期时间
     */
    boolean stringSetNx(String key, String value, long second);
    /**
     * String 原子计数器
     */
    int stringIncr(String key, int num);

}

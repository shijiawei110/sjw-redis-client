package com.sjw.sjw.redis.client.command;

/**
 * @author shijiawei
 * @version KeyCommand.java, v 0.1
 * @date 2018/11/21
 * key命令请求
 */
public interface KeyCommand {

    String deleteKey(String key);

    int keyExpire(String key, int outTime);

//    List<String> scanAllKey();
}

package com.sjw.sjw.redis.client.protocol;

import com.sjw.sjw.redis.client.constant.RedisComConstant;

import java.io.UnsupportedEncodingException;

/**
 * @author shijiawei
 * @version RedisCommandBuilder.java, v 0.1
 * @date 2018/11/27
 */
public class RedisCommandBuilder {
    private StringBuilder stringBuilder;

    protected RedisCommandBuilder(String base) {
        stringBuilder = new StringBuilder();
        stringBuilder.append(base);
    }

    public RedisCommandBuilder set(String key) {
        int size = -1;
        try {
            size = key.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuilder.append(RedisComConstant.DOLLAR).append(size).append(RedisComConstant.CRLF).
                append(key).append(RedisComConstant.CRLF);
        return this;
    }

    public RedisCommandBuilder set(int key) {
        String keyStr = String.valueOf(key);
        int size = -1;
        try {
            size = keyStr.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuilder.append(RedisComConstant.DOLLAR).append(size).append(RedisComConstant.CRLF).
                append(keyStr).append(RedisComConstant.CRLF);
        return this;
    }

    public RedisCommandBuilder append(String str) {
        stringBuilder.append(str);
        return this;
    }

    public RedisCommandBuilder append(int str) {
        stringBuilder.append(str);
        return this;
    }

    public RedisCommandBuilder crlf() {
        stringBuilder.append(RedisComConstant.CRLF);
        return this;
    }

    public RedisCommandBuilder dollar() {
        stringBuilder.append(RedisComConstant.DOLLAR);
        return this;
    }

    public RedisCommandBuilder ex() {
        stringBuilder.append(RedisComConstant.DOLLAR).append(2).append(RedisComConstant.CRLF).
                append(RedisComConstant.EX).append(RedisComConstant.CRLF);
        return this;
    }

    public RedisCommandBuilder nx() {
        stringBuilder.append(RedisComConstant.DOLLAR).append(2).append(RedisComConstant.CRLF).
                append(RedisComConstant.NX).append(RedisComConstant.CRLF);
        return this;
    }

    public String build() {
        return this.stringBuilder.toString();
    }
}

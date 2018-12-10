package com.sjw.sjw.redis.client.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sjw.sjw.redis.client.protocol.encoder.HashEncoder;
import com.sjw.sjw.redis.client.protocol.encoder.KeyEncoder;
import com.sjw.sjw.redis.client.protocol.encoder.ListEncoder;
import com.sjw.sjw.redis.client.protocol.encoder.StringEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author shijiawei
 * @version RedisCommand.java, v 0.1
 * @date 2018/11/20
 * redis请求
 */
public class RedisCommand extends RedisCommandBase implements StringEncoder, ListEncoder, KeyEncoder, HashEncoder {

    /**
     * 对象序列化 为 str
     */
    protected String objToStr(Object obj) {
        String v = JSON.toJSONString(obj);
        return v;
    }

    /**
     * str 反序列化 为对象
     */
    protected <T> T strToObj(String str, Class<T> clazz) {
        JSONObject jsonObject = JSONObject.parseObject(str);
        T obj = JSON.toJavaObject(jsonObject, clazz);
        return obj;
    }

    protected <T> List<T> strsToObjs(String[] strs, Class<T> clazz) {
        List<T> list = new ArrayList<T>(strs.length);
        for (String str : strs) {
            T obj = strToObj(str, clazz);
            list.add(obj);
        }
        return list;
    }

    protected <T> List<T> strsToObjs(List<String> strs, Class<T> clazz) {
        List<T> list = new ArrayList<T>(strs.size());
        for (String str : strs) {
            T obj = strToObj(str, clazz);
            list.add(obj);
        }
        return list;
    }

    /**
     * ----------------------------------------key 相关编码----------------------------------------
     */

    @Override
    public String unlockByPw(String password) {
        RedisCommandBuilder builder = getBuilder(UNLOCK_BASE);
        return builder.set(password).build();
    }

    @Override
    public String del(String key) {
        checkParams(key);
        RedisCommandBuilder builder = getBuilder(KEY_DEL_BASE);
        return builder.set(key).build();
    }

    @Override
    public String expire(String key, int outTime) {
        checkParams(key);
        checkNumber(outTime);
        RedisCommandBuilder builder = getBuilder(KEY_EXPIRE_BASE);
        return builder.set(key).set(outTime).build();
    }

    @Override
    public String keyScanEncode(int index){
        checkNumber(index);
        RedisCommandBuilder builder = getBuilder(KEY_SCAN_BASE);
        return builder.set(index).build();
    }

    /**
     * ----------------------------------------string 相关编码----------------------------------------
     */
    @Override
    public String stringGetEncode(String key) {
        checkParams(key);
        RedisCommandBuilder builder = getBuilder(STRING_GET_BASE);
        return builder.set(key).build();
    }

    @Override
    public String stringMgetEncode(String... keys) {
        checkParams(keys);
        RedisCommandBuilder builder = getBuilder(STRING_MGET_BASE, keys.length);
        return listSet(builder, keys);
    }

    @Override
    public String stringSetEncode(String key, String value) {
        checkParams(key, value);
        RedisCommandBuilder builder = getBuilder(STRING_SET_BASE);
        return builder.set(key).set(value).build();
    }

    @Override
    public String stringSetExEncode(String key, String value, long second) {
        checkParams(key, value);
        checkTime(second);
        String secondStr = String.valueOf(second);
        RedisCommandBuilder builder = getBuilder(STRING_SETEX_BASE);
        return builder.set(key).set(value).ex().set(secondStr).build();
    }

    @Override
    public String stringSetNxEncode(String key, String value, long second) {
        checkParams(key, value);
        checkTime(second);
        String secondStr = String.valueOf(second);
        RedisCommandBuilder builder = getBuilder(STRING_SETNX_BASE);
        return builder.set(key).set(value).ex().set(secondStr).nx().build();
    }

    @Override
    public String stringIncrEncode(String key, int num) {
        checkParams(key);
        checkNumber(num);
        String numStr = String.valueOf(num);
        RedisCommandBuilder builder = getBuilder(STRING_INCRBY_BASE);
        return builder.set(key).set(numStr).build();
    }


    /**
     * ----------------------------------------list 相关编码----------------------------------------
     */
    @Override
    public String lpushEncode(String key, String value) {
        checkParams(key, value);
        RedisCommandBuilder builder = getBuilder(LIST_LPUSH_BASE);
        return builder.set(key).set(value).build();
    }

    @Override
    public String rpopEncode(String key) {
        checkParams(key);
        RedisCommandBuilder builder = getBuilder(LIST_RPOP_BASE);
        return builder.set(key).build();
    }

    @Override
    public String llenEncode(String key) {
        checkParams(key);
        RedisCommandBuilder builder = getBuilder(LIST_LLEN_BASE);
        return builder.set(key).build();
    }

    @Override
    public String lindexEncode(String key, int index) {
        checkParams(key);
        checkNumber(index);
        RedisCommandBuilder builder = getBuilder(LIST_LINDEX_BASE);
        return builder.set(key).set(index).build();
    }

    @Override
    public String lrangeEncode(String key, int start, int end) {
        checkParams(key);
        checkRange(start, end);
        RedisCommandBuilder builder = getBuilder(LIST_LRANGE_BASE);
        return builder.set(key).set(start).set(end).build();
    }


    /**
     * ----------------------------------------hash 相关编码----------------------------------------
     */
    @Override
    public String hsetEncode(String key, String hash, String val) {
        checkParams(key, hash, val);
        RedisCommandBuilder builder = getBuilder(HASH_HSET_BASE);
        return builder.set(key).set(hash).set(val).build();
    }

    @Override
    public String hgetEncode(String key, String hash) {
        checkParams(key, hash);
        RedisCommandBuilder builder = getBuilder(HASH_HGET_BASE);
        return builder.set(key).set(hash).build();
    }

    @Override
    public String hmgetEncode(String key, String... hashs) {
        checkParams(key);
        checkParams(hashs);
        RedisCommandBuilder builder = getBuilder(HASH_HMGET_BASE, hashs.length + 1);
        return listSet(builder, key, hashs);
    }

    @Override
    public String hincrbyEncode(String key, String hash, int val) {
        checkParams(key, hash);
        RedisCommandBuilder builder = getBuilder(HASH_HINCRBY_BASE);
        return builder.set(key).set(hash).set(val).build();
    }


}

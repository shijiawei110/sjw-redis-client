package com.sjw.sjw.redis.client.protocol;

import com.sjw.sjw.redis.client.constant.RedisComConstant;
import com.sjw.sjw.redis.client.exception.SjwRedisClientException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author shijiawei
 * @version RedisCommandBase.java, v 0.1
 * @date 2018/11/26
 */
public class RedisCommandBase {

    /**
     * common 命令base
     */
    public static final String UNLOCK_BASE = "*2\r\n$4\r\nauth\r\n";

    /**
     * key命令base
     */
    public static final String KEY_DEL_BASE = "*2\r\n$3\r\ndel\r\n";
    public static final String KEY_EXPIRE_BASE = "*3\r\n$6\r\nexpire\r\n";
    public static final String KEY_SCAN_BASE = "*2\r\n$4\r\nscan\r\n";
    /**
     * String命令base
     */
    public static final String STRING_GET_BASE = "*2\r\n$3\r\nget\r\n";
    public static final String STRING_MGET_BASE = "\r\n$4\r\nmget\r\n";
    public static final String STRING_SET_BASE = "*3\r\n$3\r\nset\r\n";
    public static final String STRING_SETEX_BASE = "*5\r\n$3\r\nset\r\n";
    public static final String STRING_SETNX_BASE = "*6\r\n$3\r\nset\r\n";
    public static final String STRING_INCRBY_BASE = "*3\r\n$6\r\nincrby\r\n";

    /**
     * List命令base
     */
    public static final String LIST_LPUSH_BASE = "*3\r\n$5\r\nlpush\r\n";
    public static final String LIST_RPOP_BASE = "*2\r\n$4\r\nrpop\r\n";
    public static final String LIST_LLEN_BASE = "*2\r\n$4\r\nllen\r\n";
    public static final String LIST_LINDEX_BASE = "*3\r\n$6\r\nlindex\r\n";
    public static final String LIST_LRANGE_BASE = "*4\r\n$6\r\nlrange\r\n";

    /**
     * Hash命令base
     */
    public static final String HASH_HSET_BASE = "*4\r\n$4\r\nhset\r\n";
    public static final String HASH_HGET_BASE = "*3\r\n$4\r\nhget\r\n";
    public static final String HASH_HMGET_BASE = "\r\n$5\r\nhmget\r\n";
    public static final String HASH_HINCRBY_BASE = "*4\r\n$7\r\nhincrby\r\n";

    /**
     * 校验参数
     */
    protected void checkParams(String key) {
        if (StringUtils.isBlank(key)) {
            throw SjwRedisClientException.PARAMS_IS_BLANK;
        }
    }

    protected void checkParams(String... keys) {
        for (String key : keys) {
            checkParams(key);
        }
    }

    protected void checkParams(String key, String value) {
        if (StringUtils.isBlank(key)) {
            throw SjwRedisClientException.PARAMS_IS_BLANK;
        }
        if (StringUtils.isBlank(value)) {
            throw SjwRedisClientException.PARAMS_IS_BLANK;
        }
    }

    protected void checkTime(long time) {
        if (time <= 0) {
            throw SjwRedisClientException.PARAMS_ERROR;
        }
    }

    protected void checkNumber(int num) {
        if (num < 0) {
            throw SjwRedisClientException.PARAMS_ERROR;
        }
    }

    protected void checkRange(int start, int end) {
        if (start < 0) {
            throw SjwRedisClientException.PARAMS_ERROR;
        }
        if (end < 0) {
            throw SjwRedisClientException.PARAMS_ERROR;
        }
        if (end < start) {
            throw SjwRedisClientException.PARAMS_ERROR;
        }
    }

    protected RedisCommandBuilder getBuilder(String base) {
        return new RedisCommandBuilder(base);
    }

    protected RedisCommandBuilder getBuilder(String base, int length) {
        length++;
        String lengthStr = RedisComConstant.ASTERISK + String.valueOf(length);
        RedisCommandBuilder builder = new RedisCommandBuilder(lengthStr);
        builder.append(base);
        return builder;
    }

    protected String listSet(RedisCommandBuilder builder, String... vals) {
        for (String val : vals) {
            builder.set(val);
        }
        return builder.build();
    }

    protected String listSet(RedisCommandBuilder builder, String key, String... vals) {
        builder.set(key);
        for (String val : vals) {
            builder.set(val);
        }
        return builder.build();
    }
}

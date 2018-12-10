/**
 * fshows.com
 * Copyright (C) 2013-2018 All Rights Reserved.
 */
package com.sjw.sjw.redis.client.exception;

import java.text.MessageFormat;

/**
 * @author shijw
 * @version SjwRedisClientException.java, v 0.1 2018-10-07 20:05 shijw
 */
public class SjwRedisClientDecodeException extends RuntimeException {
    /**
     * 参数异常
     */
    public static final SjwRedisClientDecodeException NO_BYTE_READ_ERROR = new SjwRedisClientDecodeException(20001, "无字节可读(很有可能是tcp粘拆包导致)");
    public static final SjwRedisClientDecodeException DECODE_ERROR = new SjwRedisClientDecodeException(20002, "解码异常(很有可能是tcp粘拆包导致)");

    /**
     * 异常信息
     */
    protected String msg;

    /**
     * 具体异常码
     */
    protected int code;

    /**
     * 异常构造器
     *
     * @param code      代码
     * @param msgFormat 消息模板,内部会用MessageFormat拼接，模板类似：userid={0},message={1},date{2}
     * @param args      具体参数的值
     */
    private SjwRedisClientDecodeException(int code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.code = code;
        this.msg = MessageFormat.format(msgFormat, args);
    }

    /**
     * 默认构造器
     */
    private SjwRedisClientDecodeException() {
        super();
    }

    /**
     * 异常构造器
     *
     * @param message
     * @param cause
     */
    private SjwRedisClientDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 异常构造器
     *
     * @param cause
     */
    private SjwRedisClientDecodeException(Throwable cause) {
        super(cause);
    }

    /**
     * 异常构造器
     *
     * @param message
     */
    private SjwRedisClientDecodeException(String message) {
        super(message);
    }

    /**
     * 实例化异常
     *
     * @return 异常类
     */
    public SjwRedisClientDecodeException newInstance(String msgFormat, Object... args) {
        return new SjwRedisClientDecodeException(this.code, msgFormat, args);
    }


}

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
public class SjwRedisClientTryException extends Exception {
    /**
     * 参数异常
     */
    public static final SjwRedisClientTryException NO_USEFUL_CLIENT = new SjwRedisClientTryException(20001, "无可用客户端");

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
    private SjwRedisClientTryException(int code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.code = code;
        this.msg = MessageFormat.format(msgFormat, args);
    }

    /**
     * 默认构造器
     */
    private SjwRedisClientTryException() {
        super();
    }

    /**
     * 异常构造器
     *
     * @param message
     * @param cause
     */
    private SjwRedisClientTryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 异常构造器
     *
     * @param cause
     */
    private SjwRedisClientTryException(Throwable cause) {
        super(cause);
    }

    /**
     * 异常构造器
     *
     * @param message
     */
    private SjwRedisClientTryException(String message) {
        super(message);
    }

    /**
     * 实例化异常
     *
     * @return 异常类
     */
    public SjwRedisClientTryException newInstance(String msgFormat, Object... args) {
        return new SjwRedisClientTryException(this.code, msgFormat, args);
    }


}

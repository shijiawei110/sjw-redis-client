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
public class SjwRedisClientException extends RuntimeException {
    /**
     * 参数异常
     */
    public static final SjwRedisClientException POOL_INIT_REPEAT_ERROR = new SjwRedisClientException(10001, "不允许重复初始化连接池");
    public static final SjwRedisClientException OUT_OF_MAX_POOL_SIZE = new SjwRedisClientException(10002, "超过最大客户端数量");
    public static final SjwRedisClientException OUT_OF_MAX_WORK_THREAD_SIZE = new SjwRedisClientException(10003, "超过最大工作线程数量");
    public static final SjwRedisClientException GET_RES_OUT_TIME = new SjwRedisClientException(10004, "获取数据超时,关闭连接");
    public static final SjwRedisClientException PARAMS_IS_BLANK = new SjwRedisClientException(10005, "空参数错误");
    public static final SjwRedisClientException PARAMS_ERROR = new SjwRedisClientException(10006, "参数异常");
    public static final SjwRedisClientException REQUEST_PARAMS_ERROR = new SjwRedisClientException(10007, "请求异常");
    public static final SjwRedisClientException EMPTY_HOST = new SjwRedisClientException(10008, "空host错误");

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
    private SjwRedisClientException(int code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.code = code;
        this.msg = MessageFormat.format(msgFormat, args);
    }

    /**
     * 默认构造器
     */
    private SjwRedisClientException() {
        super();
    }

    /**
     * 异常构造器
     *
     * @param message
     * @param cause
     */
    private SjwRedisClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 异常构造器
     *
     * @param cause
     */
    private SjwRedisClientException(Throwable cause) {
        super(cause);
    }

    /**
     * 异常构造器
     *
     * @param message
     */
    private SjwRedisClientException(String message) {
        super(message);
    }

    /**
     * 实例化异常
     *
     * @return 异常类
     */
    public SjwRedisClientException newInstance(String msgFormat, Object... args) {
        return new SjwRedisClientException(this.code, msgFormat, args);
    }


}

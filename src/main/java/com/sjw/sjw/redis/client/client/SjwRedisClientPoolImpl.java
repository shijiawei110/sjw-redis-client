package com.sjw.sjw.redis.client.client;

import com.sjw.sjw.redis.client.exception.SjwRedisClientException;
import com.sjw.sjw.redis.client.exception.SjwRedisClientTryException;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author shijiawei
 * @version SjwRedisClientPoolImpl.java, v 0.1
 * @date 2018/11/17
 * redis连接池,改成单例模式使用
 */
public class SjwRedisClientPoolImpl implements SjwRedisClientPool {

    /**
     * 最大客户端实例数
     */
    private static final int MAX_POOL_SIZE = 99;
    /**
     * 最大work线程池数
     */
    private static final int MAX_WORK_THREAD_SIZE = 10;
    /**
     * 默认池大小
     */
    private static final int DEFAULT_POOL_SIZE = 1;
    /**
     * 默认工作线程数量
     */
    private static final int DEFAULT_WORK_GROUP_SIZE = 1;
    /**
     * 默认连接超时时间
     */
    private static final long DEFAULT_OUT_TIME_MILLS = 5000L;
    /**
     * 默认端口
     */
    private static final int DEFAULT_PORT = 6379;

    public static SjwRedisClientPool simplePool(String host) {
        return createSimpleSjwRedisPool(host, null, null);
    }

    public static SjwRedisClientPool simplePool(String host, int port) {
        return createSimpleSjwRedisPool(host, port, null);
    }

    public static SjwRedisClientPool simplePool(String host, int port, String password) {
        return createSimpleSjwRedisPool(host, port, password);
    }


    /**
     * 服务地址
     **/
    private String host;
    /**
     * 端口号
     **/
    private int port;
    /**
     * 解锁密码
     */
    private String password;
    /**
     * 连接超时毫秒时
     */
    private long outTimeMills;
    /**
     * 客户端工作线程池大小
     **/
    private int workerGroupSize;
    /**
     * 客户端池大小
     **/
    private int poolSize;
    /**
     * 客户端实例保持容器
     **/
    private SjwRedisClient[] clients;

    /**
     * 初始化标记 只能被初始化一次
     **/
    private AtomicBoolean isInitialization = new AtomicBoolean(false);


    private SjwRedisClientPoolImpl(SjwRedisClientPoolImpl.Build build) {
        host = build.host;
        password = build.password;
        if (null == build.port) {
            port = DEFAULT_PORT;
        } else {
            port = build.port;
        }
        if (null == build.workerGroupSize) {
            workerGroupSize = DEFAULT_WORK_GROUP_SIZE;
        } else {
            workerGroupSize = build.workerGroupSize;
        }
        if (null == build.poolSize) {
            poolSize = DEFAULT_POOL_SIZE;
        } else {
            poolSize = build.poolSize;
        }
        if (null == build.outTimeMills) {
            outTimeMills = DEFAULT_OUT_TIME_MILLS;
        } else {
            outTimeMills = build.outTimeMills;
        }
    }

    public static class Build {
        private String host;
        private Integer port;
        private String password;
        private Integer workerGroupSize;
        private Integer poolSize;
        private Long outTimeMills;


        public Build setHost(String host) {
            this.host = host;
            return this;
        }

        public Build setPort(int port) {
            this.port = port;
            return this;
        }

        public Build setWorkerGroupSize(int workerGroupSize) {
            this.workerGroupSize = workerGroupSize;
            return this;
        }

        public Build setPoolSize(int poolSize) {
            this.poolSize = poolSize;
            return this;
        }

        public Build setPassword(String password) {
            this.password = password;
            return this;
        }

        public Build setOutTimeMills(long outTimeMills) {
            this.outTimeMills = outTimeMills;
            return this;
        }

        public SjwRedisClientPoolImpl build() {
            return new SjwRedisClientPoolImpl(this);
        }

    }

    @Override
    public void initPool() {
        //参数大小校验
        checkInitParams();
        //只能初始化一次
        if (isInitialization.get()) {
            throw SjwRedisClientException.POOL_INIT_REPEAT_ERROR;
        }
        clients = new SjwRedisClient[poolSize];
        for (int i = 0; i < poolSize; i++) {
            SjwRedisClient sjwRedisClient = new SjwRedisClientImpl(workerGroupSize, i + 1, outTimeMills);
            boolean connectFlag = sjwRedisClient.tryConnect(host, port, password);
            if (connectFlag) {
                System.out.println(MessageFormat.format("SjwRedisClient实例[{0}]上线,服务地址 {1}:{2}", i + 1, host, String.valueOf(port)));
                clients[i] = sjwRedisClient;
            } else {
                System.out.println(MessageFormat.format("SjwRedisClient实例[{0}]连接redis服务失败,服务地址 {1}:{2}", i + 1, host, String.valueOf(port)));
            }
        }
        isInitialization.set(true);
    }


    @Override
    public synchronized SjwRedisClient getClient() throws SjwRedisClientTryException {
        for (int i = 0; i < poolSize; i++) {
            SjwRedisClient sjwRedisClient = clients[i];
            //先判断是否生成连接
            if (null == sjwRedisClient) {
                continue;
            }
            //判断是否存活
            if (!sjwRedisClient.getIsLive()) {
                continue;
            }
            //如果连接生成了再判断是否被占用
            if (!sjwRedisClient.getIsUsing()) {
                sjwRedisClient.setUsing();
                return sjwRedisClient;
            }
        }
        throw SjwRedisClientTryException.NO_USEFUL_CLIENT;
    }


    @Override
    public void closePool() {
        for (int i = 0; i < poolSize; i++) {
            SjwRedisClient sjwRedisClient = clients[i];
            if (null != sjwRedisClient) {
                sjwRedisClient.destroy();
                clients[i] = null;
            }
        }
    }


    /**
     * 校验初始化参数是否合法 并且写上默认值
     */
    private void checkInitParams() {
        if (poolSize > MAX_POOL_SIZE) {
            throw SjwRedisClientException.OUT_OF_MAX_POOL_SIZE;
        }
        if (workerGroupSize > MAX_WORK_THREAD_SIZE) {
            throw SjwRedisClientException.OUT_OF_MAX_WORK_THREAD_SIZE;
        }
        if (StringUtils.isBlank(host)) {
            throw SjwRedisClientException.EMPTY_HOST;
        }
    }

    /**
     * 简单快速生成pool 和 client
     */
    private static SjwRedisClientPool createSimpleSjwRedisPool(String host, Integer port, String password) {
        SjwRedisClientPoolImpl.Build build = new SjwRedisClientPoolImpl.Build();
        build.setHost(host);
        build.setPort(port);
        build.setPassword(password);
        SjwRedisClientPool pool = build.build();
        pool.initPool();
        return pool;
    }

}

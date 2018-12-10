package com.sjw.sjw.redis.client.client;

import com.sjw.sjw.redis.client.constant.RedisComConstant;
import com.sjw.sjw.redis.client.exception.SjwRedisClientException;
import com.sjw.sjw.redis.client.netty.ClientChannelHandlers;
import com.sjw.sjw.redis.client.netty.RedisRespone;
import com.sjw.sjw.redis.client.protocol.RedisCommand;
import com.sjw.sjw.redis.client.request.ReqTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author shijiawei
 * @version SjwRedisClientImpl.java, v 0.1
 * @date 2018/11/17
 */
@SuppressWarnings("unchecked")
public class SjwRedisClientImpl extends RedisCommand implements SjwRedisClient {


    /**
     * 客户端编号
     */
    private int clientNum;

    /**
     * 连接超时时间（ms 毫秒） 和 tcp超时时间一致
     */
    private int outTimeMills;

    /**
     * 连接实例
     */
    private Channel channel;

    /**
     * 客户点连接参数实例
     **/
    private Bootstrap bootstrap;
    /**
     * 客户端工作线程池
     **/
    private EventLoopGroup eventLoopGroup;

    /**
     * 是否连接
     **/
    private AtomicBoolean isLive = new AtomicBoolean(false);
    /**
     * 是否被线程占用
     **/
    private AtomicBoolean isUsing = new AtomicBoolean(false);


    protected SjwRedisClientImpl(int workerGroupSize, int clientNum, long timeOutMills) {
        this.clientNum = clientNum;
        this.outTimeMills = (int) timeOutMills;
        if (Epoll.isAvailable()) {
            eventLoopGroup = new EpollEventLoopGroup(workerGroupSize);
        } else {
            eventLoopGroup = new NioEventLoopGroup(workerGroupSize);
        }
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.SO_LINGER, 0);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeOutMills);
        if (Epoll.isAvailable()) {
            bootstrap.channel(EpollSocketChannel.class);
        } else {
            bootstrap.channel(NioSocketChannel.class);
        }
        bootstrap.handler(new ClientChannelHandlers());
    }

    @Override
    public int getClientNum() {
        return clientNum;
    }

    @Override
    public int getOutTimeMills() {
        return outTimeMills;
    }

    @Override
    public boolean getIsLive() {
        return isLive.get();
    }

    @Override
    public boolean getIsUsing() {
        return isUsing.get();
    }

    @Override
    public void setUsing() {
        isUsing.set(true);
    }

    @Override
    public boolean tryConnect(String host, int port, String password) {
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            if (channelFuture.isSuccess()) {
                isLive.set(true);
                channel = channelFuture.channel();
                //使用密码解锁其他命令
                if (StringUtils.isNotBlank(password)) {
                    String unlockRes = unlockByPassword(password);
                    System.out.println("sjw redis client【" + clientNum + "】初始化密码连接 -> response = " + unlockRes);
                }
            }
            return isLive.get();
        } catch (InterruptedException e) {
            return false;
        }
    }


    @Override
    public synchronized void close() {
        if (isUsing.get()) {
            isUsing.set(false);
        }
    }

    @Override
    public void destroy() {
        isLive.set(false);
        if (channel != null) {
            channel.close();
            channel.closeFuture();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 报文发送与接收核心
     */
    private RedisRespone sendRequest(String msg) {
        if (StringUtils.isBlank(msg)) {
            throw SjwRedisClientException.REQUEST_PARAMS_ERROR;
        }
//        System.out.println("sjw redis client【" + clientNum + "】发送请求 -> msg = " + msg);
        ReqTool tool = ReqTool.instance();
        tool.send(msg, channel);
        RedisRespone respone = tool.getRes(this);
        if (null == respone) {
            throw SjwRedisClientException.GET_RES_OUT_TIME;
        }
        return respone;
    }

    /**
     * redis 密码解锁
     */
    private String unlockByPassword(String password) {
        return sendRequest(unlockByPw(password)).toS();
    }


    /**
     * --------------------------------------------Key指令集--------------------------------------------
     */

    @Override
    public String deleteKey(String key) {
        return sendRequest(del(key)).toS();
    }

    @Override
    public int keyExpire(String key, int outTime) {
        return Integer.parseInt(sendRequest(expire(key, outTime)).toS());
    }

    //scan 待补充 需要重新修改 解码器支持多层*嵌套
//    @Override
//    public List<String> scanAllKey() {
//        int startIndex = 0;
//        String msg = keyScanEncode(startIndex);
//        ReqTool tool = ReqTool.instance();
//        tool.acquireLock();
//        try {
//            while (true) {
//                RedisRespone response = tool.sendRequestNoLock(msg, channel, this);
//                if (null == response) {
//                    throw SjwRedisClientException.GET_RES_OUT_TIME;
//                }
//            }
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            tool.realeaseLock();
//        }
//    }


    /**
     * --------------------------------------------String指令集--------------------------------------------
     */

    @Override
    public String stringGet(String key) {
        return sendRequest(stringGetEncode(key)).toS();
    }

    @Override
    public String[] stringMget(String... keys) {
        return sendRequest(stringMgetEncode(keys)).toM();
    }

    @Override
    public String stringSet(String key, String value) {
        return sendRequest(stringSetEncode(key, value)).toS();
    }

    @Override
    public String stringSet(String key, String value, long second) {
        return sendRequest(stringSetExEncode(key, value, second)).toS();
    }

    @Override
    public boolean stringSetNx(String key, String value, long second) {
        String response = sendRequest(stringSetNxEncode(key, value, second)).toS();
        if (RedisComConstant.SUCCESS_RES.equals(response)) {
            //设置成功
            return true;
        }
        return false;
    }

    @Override
    public int stringIncr(String key, int num) {
        String response = sendRequest(stringIncrEncode(key, num)).toS();
        return Integer.parseInt(response);
    }


    /**
     * --------------------------------------------List指令集--------------------------------------------
     */
    @Override
    public int lpush(String key, String value) {
        String response = sendRequest(lpushEncode(key, value)).toS();
        return Integer.parseInt(response);
    }

    @Override
    public int lpush(String key, Object value) {
        String val = objToStr(value);
        return lpush(key, val);
    }

    @Override
    public String rpop(String key) {
        return sendRequest(rpopEncode(key)).toS();
    }


    @Override
    public <T> T rpop(String key, Class<T> clazz) {
        String str = rpop(key);
        return strToObj(str, clazz);
    }

    @Override
    public int lsize(String key) {
        return Integer.parseInt(sendRequest(llenEncode(key)).toS());
    }

    @Override
    public String lindex(String key, int index) {
        return sendRequest(lindexEncode(key, index)).toS();
    }

    @Override
    public <T> T lindex(String key, int index, Class<T> clazz) {
        String str = lindex(key, index);
        return strToObj(str, clazz);
    }

    @Override
    public String[] lrange(String key, int start, int end) {
        return sendRequest(lrangeEncode(key, start, end)).toM();
    }

    @Override
    public <T> List<T> lrange(String key, int start, int end, Class<T> clazz) {
        String[] strs = lrange(key, start, end);
        return strsToObjs(strs, clazz);
    }


    /**
     * --------------------------------------------Hash指令集--------------------------------------------
     */
    @Override
    public int hset(String key, String hash, String val) {
        String res = sendRequest(hsetEncode(key, hash, val)).toS();
        return Integer.parseInt(res);
    }

    @Override
    public int hset(String key, String hash, Object val) {
        String str = objToStr(val);
        return hset(key, hash, str);
    }

    @Override
    public String hget(String key, String hash) {
        return sendRequest(hgetEncode(key, hash)).toS();
    }

    @Override
    public <T> T hget(String key, String hash, Class<T> clazz) {
        String str = hget(key, hash);
        return strToObj(str, clazz);
    }

    @Override
    public List<String> hmget(String key, String... hashs) {
        return sendRequest(hmgetEncode(key, hashs)).toL();
    }

    @Override
    public <T> List<T> hmget(String key, Class<T> clazz, String... hashs) {
        List<String> strs = hmget(key, hashs);
        return strsToObjs(strs, clazz);
    }

    @Override
    public int hincrby(String key, String hash, int num) {
        String res = sendRequest(hincrbyEncode(key, hash, num)).toS();
        return Integer.parseInt(res);
    }

}

package com.sjw.sjw.redis.client.request;

import com.sjw.sjw.redis.client.client.SjwRedisClient;
import com.sjw.sjw.redis.client.constant.RedisComConstant;
import com.sjw.sjw.redis.client.netty.RedisRespone;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

/**
 * @author shijiawei
 * @version ReqTool.java, v 0.1
 * @date 2018/11/20
 * 请求队列工具
 */
public class ReqTool implements ReqAndRes {

    private ReqTool() {
        syncTool = SyncTool.instance();
    }

    public static final ReqTool instance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ReqTool INSTANCE = new ReqTool();
    }

    private static final String EMPTY_STR = "";

    private SyncTool syncTool;

    private RedisRespone response;


    @Override
    public void send(String msg, Channel channel) {
        syncTool.acquire(SyncTool.LOCK);
        channel.writeAndFlush(msg);
    }

    @Override
    public RedisRespone getRes(SjwRedisClient sjwRedisClient) {
        try {
            spinGetRes(sjwRedisClient);
            RedisRespone res = response;
            response = null;
            return res;
        }finally {
            syncTool.release(SyncTool.UNLOCK);
        }
    }

    @Override
    public RedisRespone sendRequestNoLock(String msg, Channel channel, SjwRedisClient sjwRedisClient){
        channel.writeAndFlush(msg);
        spinGetRes(sjwRedisClient);
        RedisRespone res = response;
        response = null;
        return res;
    }

    /**
     * 手动获取锁
     */
    @Override
    public void acquireLock(){
        syncTool.acquire(SyncTool.LOCK);
    }

    /**
     * 手动解锁
     */
    @Override
    public void realeaseLock(){
        syncTool.release(SyncTool.UNLOCK);
    }

    @Override
    public void setRes(RedisRespone response) {
        this.response = response;
    }


    /**
     * 异步获取数据异常
     * 关闭连接 并且清理数据后 开锁防止 窜数据
     */
    private void doGetResError(SjwRedisClient sjwRedisClient) {
        //解锁
        System.out.println("sjw redis client【" + sjwRedisClient.getClientNum() + "】获取数据异常,关闭连接,[后续可以增加复活连接]");
        sjwRedisClient.destroy();
        response = null;
        //重新init这个client（后续可以实现）
    }

    /**
     * 自旋获取结果
     */
    private void spinGetRes(SjwRedisClient sjwRedisClient){
        int count = 0;
        while (true) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                doGetResError(sjwRedisClient);
            }
            if (response != null) {
                break;
            }
            count++;
            if (count > sjwRedisClient.getOutTimeMills()) {
                doGetResError(sjwRedisClient);
                break;
            }
        }
    }

}

package com.sjw.sjw.redis.client.netty;

import com.sjw.sjw.redis.client.request.ReqTool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author shijiawei
 * @version ServerHandle.java, v 0.1
 * @date 2018/8/30
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<RedisRespone> {


    /**
     * 重写连接激活
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        System.out.println("接收到一个来自redis服务器的连接 -> server addr = " + channel.remoteAddress());
    }

    /**
     * 重写连接关闭,当与server断开连接时触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        System.out.println("断开一个redis服务器的连接 -> server addr = " + channel.remoteAddress());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RedisRespone redisRespone) {
//        System.out.println("收到redis的回复:" + response);
            ReqTool.instance().setRes(redisRespone);
    }

}

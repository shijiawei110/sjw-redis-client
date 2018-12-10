package com.sjw.sjw.redis.client.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author shijiawei
 * @version ServerChannelHandlers.java, v 0.1
 * @date 2018/8/31
 * server处理管道
 */
public class ClientChannelHandlers extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        /**
         * 管理的各个处理器
         */
//        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast(new RedisDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast(new ClientHandler());
    }
}

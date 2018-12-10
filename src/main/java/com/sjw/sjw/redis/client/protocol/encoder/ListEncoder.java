package com.sjw.sjw.redis.client.protocol.encoder;

/**
 * @author shijiawei
 * @version ListEncoder.java, v 0.1
 * @date 2018/11/20
 * LIST 类型 协议编码
 */
public interface ListEncoder {
    /**
     * 左侧加入队列
     *
     * @param key
     * @return
     */
    String lpushEncode(String key, String value);

    /**
     * 右侧读取队列
     */
    String rpopEncode(String key);

    /**
     * 获取size
     */
    String llenEncode(String key);

    /**
     * 按索引获取值
     */
    String lindexEncode(String key, int index);

    /**
     * 按索引范围 获取值
     */
    String lrangeEncode(String key, int start, int end);
}

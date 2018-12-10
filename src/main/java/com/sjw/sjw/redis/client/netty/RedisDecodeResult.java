package com.sjw.sjw.redis.client.netty;


/**
 * @author shijiawei
 * @version RedisDecodeResult.java, v 0.1
 * @date 2018/11/27
 */
public class RedisDecodeResult {

    public static RedisDecodeResult success(byte[] data) {
        RedisDecodeResult result =  new RedisDecodeResult();
        result.setType(1);
        result.setData(data);
        return result;
    }

    public static RedisDecodeResult arraySuccess(byte[][] datas) {
        RedisDecodeResult result =  new RedisDecodeResult();
        result.setType(2);
        result.setDatas(datas);
        return result;
    }

    public static RedisDecodeResult fail() {
        RedisDecodeResult result =  new RedisDecodeResult();
        result.setType(0);
        return result;
    }

    private RedisDecodeResult() {}


    /**
     * 0:粘拆包异常 1:单元素回复 2:数组回复
     **/
    private int type;
    /**
     * 单元素回复数据包
     */
    private byte[] data;
    /**
     * 多元素回复数据包
     */
    private byte[][] datas;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[][] getDatas() {
        return datas;
    }

    public void setDatas(byte[][] datas) {
        this.datas = datas;
    }
}

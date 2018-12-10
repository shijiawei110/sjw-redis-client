package com.sjw.sjw.redis.client.netty;

import java.util.Arrays;
import java.util.List;

/**
 * @author shijiawei
 * @version RedisRespone.java, v 0.1
 * @date 2018/12/5
 * 接收redis 回复容器
 */
public class RedisRespone {

    public static RedisRespone nullRes() {
        RedisRespone res = new RedisRespone();
        res.type = 0;
        return res;
    }

    public static RedisRespone singleRes(String data) {
        RedisRespone res = new RedisRespone();
        res.type = 1;
        res.data = data;
        return res;
    }

    public static RedisRespone manyRes(String[] datas) {
        RedisRespone res = new RedisRespone();
        res.type = 2;
        res.datas = datas;
        return res;
    }

    public RedisRespone() {
    }

    /**
     * 回复类型 0:空回复  1:单元素 2:多元素
     */
    private int type;

    private String data;

    private String[] datas;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String[] getDatas() {
        return datas;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
    }


    public String toS() {
        if (type == 0) {
            return null;
        }
        return data;
    }

    public String[] toM() {
        if (type == 0) {
            return null;
        }
        return datas;
    }

    public List<String> toL() {
        if (type == 0) {
            return null;
        }
        return Arrays.asList(datas);
    }
}

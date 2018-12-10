package com.sjw.sjw.redis.client.netty;

import com.sjw.sjw.redis.client.constant.AsciiConstant;
import com.sjw.sjw.redis.client.exception.SjwRedisClientDecodeException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author shijiawei
 * @version RedisDecoder.java, v 0.1
 * @date 2018/11/15
 * 把 encode和decode集成到一起 可以@Sharable 实现单例
 * redis通讯协议协议
 * String类型 : 数据类型(1byte,$)  | 数据长度(n byte,不确定) | CR+LF| 数据包(N BYTE) | CR+LF
 */
public class RedisDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        //调试模式
//        testMode(in);

        //记录读游标位置 以便于产生tcp粘包拆包后恢复
        in.markReaderIndex();

        RedisDecodeResult res = decodeCore(in);
        if (0 == res.getType()) {
            //tcp协议校验失败 直接归位等待下一个tcp包
            in.resetReaderIndex();
        }
        //单元素情况
        if (1 == res.getType()) {
            RedisRespone respone = new RedisRespone();
            respone.setType(1);
            byte[] data = res.getData();
            renderRes(data, null, respone);
            out.add(respone);
        }
        //多元素情况
        if (2 == res.getType()) {
            RedisRespone respone = new RedisRespone();
            respone.setType(2);
            byte[][] datas = res.getDatas();
            renderRes(null,datas,respone);
            out.add(respone);
        }
    }

    /**
     * 根据不同回复类型 拆包 如果长度校验失败,则说明发生tcp粘拆包,进行缓冲容器复位操作
     *
     * @param in
     * @return
     */
    private RedisDecodeResult decodeCore(ByteBuf in) {
        RedisDecodeResult result;
        //获取数据类型标识
        byte dataType = in.readByte();
        switch (dataType) {
            default:
                result = RedisDecodeResult.fail();
                break;
            case '$':
                result = stringRes(in);
                break;
            case '*':
                result = arrayRes(in);
                break;
            case '+':
                result = simpleRes(in);
                break;
            case '-':
                result = simpleRes(in);
                break;
            case ':':
                result = simpleRes(in);
                break;
        }
        return result;
    }


    /**
     * resp Bulk Strings类型
     *
     * @param in
     * @return
     */
    private RedisDecodeResult stringRes(ByteBuf in) {
        int readBytes = in.readableBytes();
        int crlfCount = 0;
        byte currentAsciiByte;
        String lengthStr = "";
        Integer length = null;
        int currentLength = 0;
        byte[] result = null;
        //遍历缓冲堆外内存
        for (int i = 0; i < readBytes; i++) {
            //检测到报文正文长度大于约定长度直接快速失败
            if (length != null && length > 0 && currentLength > length) {
                return RedisDecodeResult.fail();
            }

            currentAsciiByte = in.readByte();
            //数字情况加入长度缓存,获取长度 包括-号
            if (crlfCount == 0) {
                //数字情况
                if (currentAsciiByte >= AsciiConstant.NumMin && currentAsciiByte <= AsciiConstant.NumMax) {
                    char byteChar = (char) currentAsciiByte;
                    lengthStr = lengthStr + byteChar;
                    continue;
                }
                // -号情况
                if (currentAsciiByte == AsciiConstant.JIAN) {
                    char byteChar = (char) currentAsciiByte;
                    lengthStr = lengthStr + byteChar;
                    continue;
                }
            }
            //CR和LF 情况
            if (currentAsciiByte == AsciiConstant.LF || currentAsciiByte == AsciiConstant.CR) {
                //刚读完第一个CRLF 情况
                if (crlfCount <= 1) {
                    length = Integer.parseInt(lengthStr);
                    if (length > 0) {
                        result = new byte[length];
                    }
                    crlfCount++;
                    continue;
                }
                //读完报文尾部的CRLF 情况
                if (currentLength == length) {
                    if (crlfCount > 4) {
                        //已经粘包了
                        return RedisDecodeResult.fail();
                    }
                    crlfCount++;
                    continue;
                }
                //报文中包含CRLF 情况 直接往下面走加入正文内容
            }
            //其他ascii码即是报文的正文内容
            if (currentLength > length) {
                return RedisDecodeResult.fail();
            }
            result[currentLength] = currentAsciiByte;
            currentLength++;
        }


        //空字符情况
        if (length <= 0) {
            return RedisDecodeResult.success(null);
        }

        if (null == length || currentLength != length) {
            //报文长度不一致
            return RedisDecodeResult.fail();
        }
        return RedisDecodeResult.success(result);
    }

    /**
     * 简单类型解码,用于数字，错误和简单字符三种情况
     *
     * @param in
     * @return
     */
    private RedisDecodeResult simpleRes(ByteBuf in) {
        int size = in.readableBytes();
        byte[] res = new byte[size - 2];
        for (int i = 0; i < size; i++) {
            byte currentAsciiByte = in.readByte();
            if (currentAsciiByte == AsciiConstant.LF || currentAsciiByte == AsciiConstant.CR) {
                //跳过读取crlf
                in.readableBytes();
            } else {
                res[i] = currentAsciiByte;
            }
        }
        return RedisDecodeResult.success(res);
    }

    /**
     * 数组回复模式
     */
    private RedisDecodeResult arrayRes(ByteBuf in) {
        try {
            //首先读取数组个数
            Integer arrayLength = getLength(in);

            //长度小于0 说明是空数组
            if (arrayLength <= 0) {
                return RedisDecodeResult.success(null);
            }
            //依次读取数组每个元素
            byte[][] datas = new byte[arrayLength][];
            for (int i = 0; i < arrayLength; i++) {
                RedisDecodeResult result = getArrayData(in);
                byte[] data = result.getData();
                datas[i] = data;
            }
            return RedisDecodeResult.arraySuccess(datas);
        } catch (SjwRedisClientDecodeException e) {
            //复位ByteBuf
            System.out.println("【tcp解码错误】进行复位 msg:" + e.getMessage());
            return RedisDecodeResult.fail();
        }
    }

    /**
     * 对数组每一个元素进行获取
     */
    private RedisDecodeResult getArrayData(ByteBuf in) {
        byte currentAsciiByte;

        //校验是否有$
        currentAsciiByte = safeRead(in);
        if (currentAsciiByte != AsciiConstant.DOLLAR) {
            throw SjwRedisClientDecodeException.DECODE_ERROR;
        }
        //开始读取长度
        Integer length = getLength(in);

        //长度小于0说明为null
        if (length <= 0) {
            return RedisDecodeResult.success(null);
        }

        //根据长度读取正文数据
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            currentAsciiByte = safeRead(in);
            data[i] = currentAsciiByte;
        }
        //验证末尾crlf
        currentAsciiByte = safeRead(in);
        if (AsciiConstant.CR != currentAsciiByte) {
            throw SjwRedisClientDecodeException.DECODE_ERROR;
        }
        currentAsciiByte = safeRead(in);
        if (AsciiConstant.LF != currentAsciiByte) {
            throw SjwRedisClientDecodeException.DECODE_ERROR;
        }
        return RedisDecodeResult.success(data);

    }

    /**
     * 通用读取RESP协议的长度,如果粘拆包就返回null
     *
     * @return
     */
    private Integer getLength(ByteBuf in) {
        byte currentAsciiByte;
        String lengthStr = "";
        boolean cr = true;
        boolean lf = true;
        while (cr || lf) {
            currentAsciiByte = safeRead(in);
            if (currentAsciiByte == AsciiConstant.LF) {
                lf = false;
                continue;
            }
            if (currentAsciiByte == AsciiConstant.CR) {
                cr = false;
                continue;
            }
            char byteChar = (char) currentAsciiByte;
            lengthStr = lengthStr + byteChar;
        }
        return Integer.parseInt(lengthStr);
    }

    /**
     * 安全读取字节,读不出来就抛异常 给外部捕获直接复位缓存字节
     *
     * @return
     */
    private byte safeRead(ByteBuf in) {
        if (in.readableBytes() <= 0) {
            throw SjwRedisClientDecodeException.NO_BYTE_READ_ERROR;
        }
        return in.readByte();
    }


    /**
     * 打印调试模式
     */
    private void testMode(ByteBuf in) {
        in.markReaderIndex();
        int size = in.readableBytes();
        byte[] testData = new byte[size];
        for (int i = 0; i < size; i++) {
            testData[i] = in.readByte();
        }
        try {
            String kk = new String(testData, "UTF-8");
            System.out.print("【redis协议解码器调试模式数据】: " + kk);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        in.resetReaderIndex();
    }


    /**
     * 渲染数据为 redisResponse
     */
    private void renderRes(byte[] data, byte[][] datas, RedisRespone respone) {
        int type = respone.getType();
        //单回复
        if (1 == type) {
            //空回复情况
            if (null == data) {
                respone.setType(0);
                return;
            }
            String resStr;
            try {
                resStr = new String(data, "UTF-8");
                respone.setData(resStr);
                return;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                respone.setType(0);
                return;
            }
        }

        //数组回复
        if (2 == type) {
            //空回复情况
            if (null == datas) {
                respone.setType(0);
                return;
            }
            int length = datas.length;
            String[] strs = new String[length];
            for (int i = 0; i < length; i++) {
                byte[] currentDta = datas[i];
                if(null == currentDta){
                    //数据为空
                    strs[i] = null;
                    continue;
                }
                try {
                    String resStr = new String(currentDta, "UTF-8");
                    strs[i] = resStr;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    strs[i] = null;
                }
            }
            respone.setDatas(strs);
            return;
        }
    }

}

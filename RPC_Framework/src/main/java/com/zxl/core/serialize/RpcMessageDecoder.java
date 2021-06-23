package com.zxl.core.serialize;

import com.zxl.commons.entity.RpcConstant;
import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;
import com.zxl.commons.enums.RpcRequestTypeEnum;
import com.zxl.core.serialize.impl.KryoSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/*
 *      0     1     2     3       4      5     6     7     8     9          10
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------------+
 *   |   magic   code        |version | full length         | codec     | requestType |
 *   +-----------------------+--------+---------------------+-----------+-------------+
 *   |                                                                                |
 *   |                                         body                                   |
 *   |                                                                                |
 *   |                                        ... ...                                 |
 *   +--------------------------------------------------------------------------------+
 * 4B  magic code（魔法数） 1B version（版本） 4B full length（消息长度） 1B codec（序列化类型）
 * 1B requestType(请求类型，用于告知netty的解码器自己是RpcRequest还是RpcResponse，方便解码器工作)
 * body（object类型数据）
 */
import java.util.Arrays;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        // lengthFieldOffset: 获得最大长度的偏移  4+1=5
        // lengthFieldLength: 最大长度定义的byte长度 4
        // lengthAdjustment:  -9 full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: 0：不要跳过任何byte we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(RpcConstant.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      Maximum frame length. It decide the maximum length of data that can be received.
     *                            If it exceeds, the data will be discarded.
     * @param lengthFieldOffset   Length field offset. The length field is the one that skips the specified length of byte.
     * @param lengthFieldLength   The number of bytes in the length field.
     * @param lengthAdjustment    The compensation value to add to the value of the length field
     * @param initialBytesToStrip Number of bytes skipped.
     *                            If you need to receive all of the header+body data, this value is 0
     *                            if you only want to receive the body data, then you need to skip the number of bytes consumed by the header.
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    //解码函数
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            //获得byteBuffer
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstant.HEAD_LENGTH) {//frame数据的长度至少要满足协议才行
                try {
                    checkMagicNumber(frame);
                    checkVersion(frame);
                    //得到ByteBuf最大长度
                    int fullLength = frame.readInt();
                    byte codec = frame.readByte();
                    byte requsetType = frame.readByte();
                    byte[] bs = new byte[fullLength - RpcConstant.HEAD_LENGTH];
                    frame.readBytes(bs);
                    Serialization serialization = KryoSerialization.newSingletonInstance();
                    Object obj = null;
                    if(requsetType == RpcRequestTypeEnum.RPC_REQUEST_TYPE.getTpye()){
                         obj = serialization.unSerialize(bs, RpcRequest.class);
                    }else{
                         obj = serialization.unSerialize(bs, RpcResponse.class);
                    }
                    return obj;
                } catch (Exception e) {
                    log.printf("Decode frame error!\r\n", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }else{
                //协议不对
                log.print("解码失败，长度小于协议最小长度\r\n");
                return null;
            }

        }
        return decoded;
    }

    private void checkVersion(ByteBuf in) {
        // 检查版本
        byte version = in.readByte();
        if (version != RpcConstant.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        // 读前4个魔法并验证
        int len = RpcConstant.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstant.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

}

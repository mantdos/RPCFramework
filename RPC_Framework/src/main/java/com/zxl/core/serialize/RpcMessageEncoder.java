package com.zxl.core.serialize;

import com.zxl.commons.entity.RpcConstant;
import com.zxl.commons.entity.RpcMessage;
import com.zxl.commons.entity.RpcRequest;
import com.zxl.commons.entity.RpcResponse;
import com.zxl.commons.enums.RpcRequestTypeEnum;
import com.zxl.core.serialize.impl.KryoSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


//通过netty框架发送数据的固定类型
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


import java.io.Serializable;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;


//要定义一个统一的格式来让RPC消费方和服务方发送数据，
// 通过泛型的方式指定给MessageToByteEncoder,在调用encode方法时会自动校验msg是否实现或继承自T
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    public RpcMessageEncoder() {
    }

    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out){
        try{
            out.writeBytes(RpcConstant.MAGIC_NUMBER);
            out.writeByte(RpcConstant.VERSION);
            //添加完成后根据长度在这个位置添加一个数据总长度
            out.writerIndex(out.writerIndex() + 4);
            out.writeByte(msg.getCodec());
            out.writeByte(msg.getRequsetType());
            byte[] bodyBytes = null;
            int fullLength = RpcConstant.HEAD_LENGTH;//编码格式的前缀长度
            Serialization serialization = KryoSerialization.newSingletonInstance();
            byte[] bytes = null;
            if(msg.getRequsetType()== RpcRequestTypeEnum.RPC_REQUEST_TYPE.getTpye()){
                bytes = serialization.serialize((RpcRequest) msg.getBody(), RpcRequest.class);
            }else{
                bytes = serialization.serialize((RpcResponse) msg.getBody(), RpcResponse.class);
            }
            fullLength+= bytes.length;
            if(fullLength>RpcConstant.MAX_FRAME_LENGTH){
                log.print("RPC：Netty发送数据超过最大长度\n");
                return;
            }
            out.writeBytes(bytes);
            //当前写入到的最后一个byte的索引
            int writeIndex = out.writerIndex();
            //设置当前从第5位开始继续写
            out.writerIndex(writeIndex - fullLength + RpcConstant.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        }catch (Exception e){
            log.print("Netty 编码失败\n");
        }

    }
}
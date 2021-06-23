package com.zxl.commons.entity;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
@Builder
public class RpcMessage {

    private byte codec;

    private byte requsetType;

    private  Object body;
}

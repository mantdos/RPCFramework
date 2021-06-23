package com.zxl.commons.entity;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
public class RpcConstant {
    /**
     * Magic number. Verify RpcMessage
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'z', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    //version information
    public static final byte VERSION = 1;
    public static final byte HEAD_LENGTH = 11;
    //最大发送数据8M
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}

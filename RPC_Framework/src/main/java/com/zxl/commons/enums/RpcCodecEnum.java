package com.zxl.commons.enums;

import lombok.*;

@AllArgsConstructor
@Getter
@ToString
/**
 * 用于定义zookeeper的相关信息，例如zk的配置文件信息、配置文件地址键值
 */
public enum RpcCodecEnum {

    KRYO((byte) 0x01,"Kryo"),
    JDK((byte) 0x02,"Jdk"),
    PROTOSTUFF((byte) 0x03,"Protostuff"),
    JSON((byte) 0x04,"Jackson");

    private final byte codec;
    private final String name;


    public static String getCodeName(byte c){
      for(RpcCodecEnum e:RpcCodecEnum.values())  {
          if(e.getCodec()==c){
              return e.getName();
          }
      }
      return "";
    }

}

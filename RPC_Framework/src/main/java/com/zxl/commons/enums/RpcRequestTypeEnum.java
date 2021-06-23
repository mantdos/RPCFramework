package com.zxl.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
/**
 * 用于定义zookeeper的相关信息，例如zk的配置文件信息、配置文件地址键值
 */
public enum RpcRequestTypeEnum {

    RPC_REQUEST_TYPE((byte) 0x01,"RpcRequest"),
    RPC_RESPONSE_TYPE((byte) 0x02,"RpcResponse");

    private final byte tpye;
    private final String name;


    public static String getTypeName(byte c){
      for(RpcRequestTypeEnum e: RpcRequestTypeEnum.values())  {
          if(e.getTpye()==c){
              return e.getName();
          }
      }
      return "";
    }

}

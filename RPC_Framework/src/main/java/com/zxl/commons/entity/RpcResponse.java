package com.zxl.commons.entity;

import com.zxl.commons.enums.RpcResponseCodeEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//Rpc返回类
@Data
public class RpcResponse implements Serializable {
    //这一次Rpc调用的序列号
    private String requestID;

    //返回的对象实例
    private Object object;

    //返回代码号
    private Integer code;

    //返回的代码提示
    private String message;

    // 如果失败了，则返回一个空的响应类，并报告错误内容
    public static RpcResponse fail(String message){
        RpcResponse response = new RpcResponse();
        response.setCode(RpcResponseCodeEnum.FAIL.getCode());
        response.setMessage(message);
        return response;
    }

    //成功，返回响应
    public static RpcResponse success(Object object,String requestID){
        RpcResponse response = new RpcResponse();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setObject(object);
        response.setRequestID(requestID);
        return response;
    }

    public String getRequestID() {
        return requestID;
    }
}

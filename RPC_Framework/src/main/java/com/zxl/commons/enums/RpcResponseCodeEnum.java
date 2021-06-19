package com.zxl.commons.enums;

import lombok.*;

@AllArgsConstructor
@Getter
@ToString
/**
 * 提供方返回给客户方的类的信息定义
 */
public enum RpcResponseCodeEnum {
    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");
    private final int code;

    private final String message;
}

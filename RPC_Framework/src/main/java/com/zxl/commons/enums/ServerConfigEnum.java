package com.zxl.commons.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServerConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),
    SERVER_PORT("rpc.server.port");

    private final String propertyValue;
}

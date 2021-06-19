package com.zxl.core.loadbalance;

import java.util.List;

public interface LoadBanlance {
    public String selectServiceAddrass(List<String> serviceAddrasses);
}

package com.zxl.core.loadbalance.impl;

import com.zxl.core.loadbalance.LoadBanlance;

import java.util.List;
import java.util.Random;

public final class RandomLoadBanlance implements LoadBanlance {

    public String selectServiceAddrass(List<String> serviceAddrasses) {
        Random random = new Random();
        return serviceAddrasses.get(random.nextInt(serviceAddrasses.size()));
    }
}

package com.zxl.customer;

import com.zxl.interface1.Calculate;
import com.zxl.customer.Impl.CalculateImpl;

import java.io.IOException;

public class CustomerTest {
    public static void main(String[] args) throws IOException {
        Calculate calculate = new CalculateImpl();
        System.out.println(calculate.add(7,9));
    }
}

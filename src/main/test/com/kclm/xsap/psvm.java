package com.kclm.xsap;

import java.math.BigDecimal;

public class psvm {
    public static void main(String[] args) {
        BigDecimal bigDecimal=new BigDecimal(9.9);
        System.out.println(bigDecimal.setScale(0, BigDecimal.ROUND_DOWN));
    }
}

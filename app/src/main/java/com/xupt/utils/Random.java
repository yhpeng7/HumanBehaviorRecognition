package com.xupt.utils;

public class Random {
    public static String randomVerifyCode() {
        StringBuilder str = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}

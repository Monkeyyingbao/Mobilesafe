package com.itsafe.phone.utils;

import java.io.UnsupportedEncodingException;

/**
 * 对字符串的加密算法
 * Created by Hello World on 2016/3/24.
 */
public class EncodeUtils {
    /**
     *
     * @param s 原字符串
     * @return 加密后的字符串
     */
    public static String encode(String s,byte seed) {
        try {
            byte[] bytes = s.getBytes("gbk");
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] ^= seed;
            }
            return new String(bytes, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}

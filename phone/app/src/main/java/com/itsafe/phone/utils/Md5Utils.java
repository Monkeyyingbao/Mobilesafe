package com.itsafe.phone.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Hello World on 2016/3/10.
 */
public class Md5Utils {
    /**
     *
     * @param str 需要加密的字符串
     * @return 加密后的MD5值
     */
    public static String encode(String str) {
        String res = "";
        String s = "";
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            //MD5加密后的字节数组
            byte[] digest = md.digest(str.getBytes());
            //把字节数组转成字符串
            // byte 8bit  4bit 十六进制   2位十六进制
            for (byte b: digest) {//数组 Iterable
                //把一个字节转成十六进制 8 >> 2
                //去掉一个int类型前3个字节 0a
                int d = b & 0x000000ff;
                s = Integer.toHexString(d);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                res = res + s;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return res;
    }
}

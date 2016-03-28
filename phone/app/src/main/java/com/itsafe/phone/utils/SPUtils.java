package com.itsafe.phone.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 封装SharedPreferences对象的工具类,方便读写配置文件
 * Created by Hello World on 2016/3/5.
 */
public class SPUtils {
    /**
     *
     * @param context 调用者的上下文
     * @param key   键
     * @param value 值
     */
    public static void putBoolean(Context context,String key,Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG,0);
        sp.edit().putBoolean(key, value).commit();
    }
    public static boolean getBoolean(Context context,String key,Boolean defaultval) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG,0);
        return sp.getBoolean(key, defaultval);
    }

    public static void putString(Context context,String key,String value) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG,0);
        sp.edit().putString(key, value).commit();
    }
    public static String getString(Context context,String key,String defaultval) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG,0);
        return sp.getString(key, defaultval);
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG, 0);
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defaultval) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG, 0);
        return sp.getInt(key, defaultval);
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG, 0);
        sp.edit().putLong(key, value).commit();
    }

    public static long getLong(Context context, String key, long defaultval) {
        SharedPreferences sp = context.getSharedPreferences(StrUtils.SP_CONFIG, 0);
        return sp.getLong(key, defaultval);
    }

}

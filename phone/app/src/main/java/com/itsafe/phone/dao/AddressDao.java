package com.itsafe.phone.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 归属地数据库的Dao层封装
 * Created by Hello World on 2016/3/20.
 */
public class AddressDao {
    public static final String DBPATH = "/data/data/com.itsafe.phone/files/address.db";

    /**
     * @param number 手机号或者固定电话
     * @return 给属地信息
     */
    public static String getLocation(String number) {
        String location = "";
        //判断是否是手机号
        Pattern p = Pattern.compile("1[34578]{1}[0-9]{9}");
        Matcher m = p.matcher(number);
        boolean b = m.matches();
        if (b) {
            location = getMobileLocation(number.substring(0, 7));
        } else {
            if (number.charAt(1) == '1' || number.charAt(1) == '2') {
                //两位的区号
                location = getPhoneLocation(number.substring(1, 3));
            } else {
                //三位的区号
                location = getPhoneLocation(number.substring(1, 4));
            }
        }
        return location.substring(0,location.length()-2);
    }

    /**
     * 手机号的归属地
     *
     * @param mobileNumber 手机号的前7位
     * @return 归属地信息
     */
    public static String getMobileLocation(String mobileNumber) {
        String location = "未知截掉";
        //获取数据库
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATH, null, SQLiteDatabase.OPEN_READONLY);
        //查询
        Cursor cursor = database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{mobileNumber});
        if (cursor.moveToNext()) {
            //获取归属地信息
            location = cursor.getString(0);
        }
        return location;
    }

    /**
     * @param phoneNumber 固话的区号
     * @return 归属地
     */
    private static String getPhoneLocation(String phoneNumber) {
        String location = "未知截掉";
        //获取数据库
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATH, null, SQLiteDatabase.OPEN_READONLY);
        //查询
        Cursor cursor = database.rawQuery("select location from data2 where area = ?", new String[]{phoneNumber});
        if (cursor.moveToNext()) {
            //获取归属地信息
            location = cursor.getString(0);
        }
        return location;
    }
}

package com.itsafe.phone.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库的dao层封装
 * Created by Hello World on 2016/3/29.
 */
public class AntiVirusDao {
    public static final String DBPATHVIRUS = "/data/data/com.itsafe.phone/files/antivirus.db";

    /**
     * @param md5
     *      文件的MD5值
     * @return
     *     是否是病毒
     */
    public static boolean isVirus(String md5) {
        boolean isVirus = false;
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHVIRUS, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select 1 from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext()) {
            //有记录
            isVirus = true;
        }

        cursor.close();
        database.close();

        return isVirus;
    }

    /**
     * @return
     *     获取当前病毒数据库的版本号
     */
    public static int getCurrentVirusVersion(){
        int versionCode = -1;
        // select subcnt from version ;
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHVIRUS, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select subcnt from version", null);

        if (cursor.moveToNext()) {
            versionCode = cursor.getInt(0);
        }

        cursor.close();
        database.close();
        return versionCode;
    }

    /**
     * @param newVersion
     *      病毒库新的版本号
     */
    public static void updateVirusVersion(int newVersion){
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHVIRUS, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("subcnt", newVersion);
        database.update("version", values , null, null);

        database.close();
    }

    /**
     * @param md5
     *      病毒MD5值
     * @param desc
     *      病毒的描述信息
     */
    public static void updateVirus(String md5,String desc){

        SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHVIRUS, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("type", 6);
        values.put("name", "Android.Troj.AirAD.a");
        values.put("md5", md5);
        values.put("desc", desc);
        database.insert("datable", null, values );
        database.close();
    }
}

package com.itsafe.phone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 黑名单数据库
 * Created by Hello World on 2016/3/16.
 */
public class BlackDB extends SQLiteOpenHelper {
    //版本号
    private static final int VERSION = 1;
    //短信拦截
    private static final int SMS_MODE = 1 << 0;//0 1
    //电话拦截
    private static final int PHONE_MODE = 1 << 1;//1 0
    //全部拦截
    private static final int ALL_MODE = SMS_MODE | PHONE_MODE;//1 1

    public BlackDB(Context context) {
        super(context, "black.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //第一次创建执行
        //创建表的操作
        db.execSQL("create table blacktb(_id integer primary key autoincrement,phone text,mode integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //版本号发生变化执行此方法
        db.execSQL("drop table blacktb");
        onCreate(db);
    }
}

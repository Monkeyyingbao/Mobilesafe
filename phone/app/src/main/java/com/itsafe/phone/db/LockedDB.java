package com.itsafe.phone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by Hello World on 2016/3/30.
 */
public class LockedDB extends SQLiteOpenHelper{

    public static final String LOCKED_TB = "locked_tb";//表名
    public static final String PACKNAME = "packname";//列名
    public static final Uri URI = Uri.parse("content://itheima13.locked");


    public LockedDB(Context context) {
        super(context, "locked.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table locked_tb(_id integer primary key autoincrement,packname text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table locked_tb");
        onCreate(db);
    }
}

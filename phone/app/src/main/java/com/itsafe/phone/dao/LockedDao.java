package com.itsafe.phone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itsafe.phone.db.LockedDB;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁数据库的业务dao
 * Created by Hello World on 2016/3/30.
 */
public class LockedDao {
    private LockedDB mLockedDB;

    private Context mContext;

    public LockedDao(Context context) {
        mLockedDB = new LockedDB(context);
        mContext = context;
    }

    /**
     * 对一个app完成加锁的操作
     *
     * @param packName
     */
    public void addLockedPackName(String packName) {

        SQLiteDatabase database = mLockedDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LockedDB.PACKNAME, packName);
        database.insert(LockedDB.LOCKED_TB, null, values);
        database.close();

        //发送通知
        mContext.getContentResolver().notifyChange(LockedDB.URI,null);

    }

    /**
     * 对一个app完成解锁的操作
     *
     * @param packName
     */
    public void rmeoveLockedPackName(String packName) {

        SQLiteDatabase database = mLockedDB.getWritableDatabase();
        database.delete(LockedDB.LOCKED_TB, LockedDB.PACKNAME + "=?", new String[]{packName});
        database.close();

        //发送通知
        mContext.getContentResolver().notifyChange(LockedDB.URI,null);

    }

    /**
     * 判断app是否加锁
     *
     * @param packName app包名
     * @return 是否加锁
     */
    public boolean isLocked(String packName) {
        boolean res = false;

        SQLiteDatabase database = mLockedDB.getReadableDatabase();
        //database.rawQuery("select 1 from " + LockedDB.LOCKED_TB + " where " + LockedDB.PACKNAME + " =? ",new String[]{packName});
        Cursor cursor = database.query(LockedDB.LOCKED_TB, null, LockedDB.PACKNAME + "=?", new String[]{packName}, null, null, null);
        if (cursor.moveToNext()) {
            res = true;
        }
        cursor.close();
        database.close();
        return res;
    }

    public List<String> getAllLockedAppPackName() {
        List<String> lockedPackNames = new ArrayList<>();
        SQLiteDatabase database = mLockedDB.getReadableDatabase();
        Cursor cursor = database.query(LockedDB.LOCKED_TB, new String[]{LockedDB.PACKNAME}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            lockedPackNames.add(cursor.getString(0));
        }

        cursor.close();
        database.close();
        return lockedPackNames;
    }
}

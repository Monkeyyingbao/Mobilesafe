package com.itsafe.phone.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itsafe.phone.db.BlackDB;
import com.itsafe.phone.domain.BlackBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 对黑名单数据的操作
 * Created by Hello World on 2016/3/16.
 */
public class BlackDao {
    //黑名单数据库
    private BlackDB mBlackDB;
    public BlackDao(Context context) {
        mBlackDB = new BlackDB(context);
    }

    /**
     * 添加黑名单数据
     * @param phone 黑名单号码
     * @param mode 拦截模式
     *
     */
    public void add(String phone, int mode) {
        //1.获取数据库
        SQLiteDatabase database = mBlackDB.getWritableDatabase();
        //2.设置数据
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        //3.添加数据
        database.insert("blacktb",null,values);
        //关闭数据库
        database.close();
    }

    /**
     * 返回所有的黑名单数据
     * @return
     */
    public List<BlackBean> findAll() {
        List<BlackBean> datas = new ArrayList<>();
        SQLiteDatabase database = mBlackDB.getReadableDatabase();
        Cursor cursor = database.rawQuery("selece" + "phone" + "," + "mode" + "from" + "blacktb", null);
        BlackBean data =null;
        while (cursor.moveToNext()) {
            //有数据
            //1.封装数据
            data = new BlackBean();
            data.setPhong(cursor.getString(0));
            data.setMode(cursor.getInt(1));
            datas.add(data);
        }
        return datas;
    }
}

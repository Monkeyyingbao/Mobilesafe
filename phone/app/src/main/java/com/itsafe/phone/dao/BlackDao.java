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
     *
     * @param phone 黑名单号码
     * @param mode  拦截模式
     *              SMS_MODE短信 PHONE_MODE电话 ALL_MODE全部
     */
    public void add(String phone, int mode) {
        //1.获取数据库
        SQLiteDatabase database = mBlackDB.getWritableDatabase();
        //2.设置数据
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        //3.添加数据
        database.insert("blacktb", null, values);
        //关闭数据库
        database.close();
    }

    /**
     * @param bean 黑名单数据的对象
     */
    public void add(BlackBean bean) {
        add(bean.getPhong(), bean.getMode());
    }

    /**
     * @param phone 要删除的黑名单号码
     * @return true 删除成功 false 删除失败
     */
    public boolean delete(String phone) {
        //1.获取数据库
        SQLiteDatabase database = mBlackDB.getWritableDatabase();
        //2.删除
        int result = database.delete("blacktb", "phone=?", new String[]{phone});
        //3.数据库的关闭
        database.close();
        return result > 0;
    }

    /**
     * 如果有 先删除在添加
     * 如果没有直接添加
     * @param phone 修改黑名单号码
     * @param mode 修改黑名单模式
     */
    public void update(String phone, int mode) {
        //先删除
        delete(phone);
        //后添加
        add(phone,mode);
    }
    /**
     * 返回所有的黑名单数据
     *
     * @return
     */
    public List<BlackBean> findAll() {
        List<BlackBean> datas = new ArrayList<>();
        //获取只读的数据库
        SQLiteDatabase database = mBlackDB.getReadableDatabase();
        Cursor cursor = database.query("blacktb", new String[]{"phone", "mode"}, null, null, null, null, null);
        BlackBean data = null;
        while (cursor.moveToNext()) {
            //有数据
            //1.封装数据
            data = new BlackBean();
            //设置黑名单号码
            data.setPhong(cursor.getString(0));
            //设置拦截模式
            data.setMode(cursor.getInt(1));
            datas.add(data);
        }
        return datas;
    }
}

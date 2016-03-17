package com.itsafe.phone.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.itsafe.phone.domain.ContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取所有联系人的dao
 * Created by Hello World on 2016/3/13.
 */
public class ContactsDao {
    //获取短信联系人
    public static List<ContactBean> getSmsContacts(Context context) {
        List<ContactBean> datas = new ArrayList<>();
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, null);
        ContactBean bean = null;
        while (cursor.moveToNext()) {
            bean = new ContactBean();
            bean.setName("sms");
            bean.setPhone(cursor.getString(0));
            //添加
            datas.add(bean);
        }
        cursor.close();
        return datas;
    }
    //获取通话联系人
    public static List<ContactBean> getTelContacts(Context context) {
        List<ContactBean> datas = new ArrayList<>();
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"name", "number"}, null, null, null);
        ContactBean bean = null;
        while (cursor.moveToNext()) {
            bean = new ContactBean();
            bean.setName(cursor.getString(0));
            bean.setPhone(cursor.getString(1));
            //添加
            datas.add(bean);
        }
        cursor.close();
        return datas;
    }
    //获取好友
    public static List<ContactBean> getContacts(Context context) {
        List<ContactBean> datas = new ArrayList<>();
        //uri:content://contacts/raw_contacts
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");
        //内容提供者
        ContactBean bean = null;
        String raw_contact_id = null;
        Cursor cursor2 = null;
        String mimeType = null;
        String data = null;
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"name_raw_contact_id"}, null, null, null);
        while (cursor.moveToNext()) {
            bean = new ContactBean();
            raw_contact_id = cursor.getString(0);
            cursor2 = context.getContentResolver().query(uriData, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{raw_contact_id}, null);
            //System.out.println(cursor.getString(0));
            while (cursor2.moveToNext()) {
                //类型
                mimeType = cursor2.getString(1);
                //数据
                data = cursor2.getString(0);
                if (mimeType.equals("vnd.android.cursor.item/phone_v2")) {
                    //电话
                    bean.setPhone(data);
                }else if (mimeType.equals("vnd.android.cursor.item/name")) {
                    //姓名
                    bean.setName(data);
                }
            }
            //关闭游标
            cursor2.close();
            //添加数据
            datas.add(bean);
        }
        //关闭游标
        cursor.close();
        return datas;
    }
}

package com.itsafe.phone.dao;

import android.test.AndroidTestCase;

import com.itsafe.phone.db.BlackDB;
import com.itsafe.phone.domain.BlackBean;

/**
 * Created by Hello World on 2016/3/16.
 */
public class TestBlack extends AndroidTestCase {

    public void testLocation() {
        System.out.println(AddressDao.getLocation("13836639678"));
    }
    public void testAdd() {
        //测试添加黑名单数据
        BlackDao blackDao = new BlackDao(getContext());
        for (int i = 0; i < 100; i++) {
            blackDao.add("110" + i, BlackDB.PHONE_MODE);
        }
    }

    //测试查询数据
    public void testFindAll() {
        BlackDao blackDao = new BlackDao(getContext());
        for (BlackBean bean : blackDao.findAll()) {
            System.out.println(bean);
        }
    }
}

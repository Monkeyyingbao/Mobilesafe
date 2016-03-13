package com.itsafe.phone.dao;

import android.test.AndroidTestCase;

/**
 * Created by Hello World on 2016/3/13.
 */
public class ContactsDaoTest extends AndroidTestCase {

    public void testGetContacts() {
        ContactsDao.getContacts(getContext());
    }

}
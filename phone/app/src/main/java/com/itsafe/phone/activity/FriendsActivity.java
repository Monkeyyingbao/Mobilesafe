package com.itsafe.phone.activity;

import com.itsafe.phone.dao.ContactsDao;
import com.itsafe.phone.domain.ContactBean;

import java.util.List;

/**
 * 显示所有好友信息的界面
 */
public class FriendsActivity extends BaseSmsTelFriendsActivity {
    @Override
    public List<ContactBean> getDatas() {
        return ContactsDao.getContacts(getApplicationContext());
    }
}

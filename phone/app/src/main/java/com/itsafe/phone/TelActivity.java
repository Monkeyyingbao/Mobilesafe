package com.itsafe.phone;

import com.itsafe.phone.dao.ContactsDao;
import com.itsafe.phone.domain.ContactBean;

import java.util.List;

/**
 * Created by Hello World on 2016/3/17.
 */
public class TelActivity extends BaseSmsTelFriendsActivity {
    @Override
    public List<ContactBean> getDatas() {
        return ContactsDao.getTelContacts(getApplicationContext());
    }
}

package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itsafe.phone.R;
import com.itsafe.phone.view.SettingCenterItem;

public class AToolActivity extends Activity {

    private SettingCenterItem mSci_phonequery;
    private SettingCenterItem mSci_servicequery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initEvent();
    }

    private void initEvent() {
        //事件
        SettingCenterItem.OnToggleChangedListener listener = new SettingCenterItem.OnToggleChangedListener() {
            @Override
            public void onToggleChanged(View view, boolean isOpen) {
                switch (view.getId()) {
                    case R.id.sci_atool_mobilephone:
                        //电话查询
                        Intent phone = new Intent(AToolActivity.this, PhoneLocationActivity.class);
                        startActivity(phone);
                        break;
                    case R.id.sci_atool_servicephone:
                        //服务号码
                        Intent serviceNumber = new Intent(AToolActivity.this, ServiceNumberActivity.class);
                        startActivity(serviceNumber);
                        break;
                }
            }
        };

        mSci_phonequery.setOnToggleChangedListener(listener);
        mSci_servicequery.setOnToggleChangedListener(listener);
    }

    private void initView() {
        setContentView(R.layout.activity_atool);
        mSci_phonequery = (SettingCenterItem) findViewById(R.id.sci_atool_mobilephone);
        mSci_servicequery = (SettingCenterItem) findViewById(R.id.sci_atool_servicephone);
    }
}

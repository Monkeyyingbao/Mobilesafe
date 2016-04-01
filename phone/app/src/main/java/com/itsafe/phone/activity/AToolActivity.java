package com.itsafe.phone.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.itsafe.phone.R;
import com.itsafe.phone.utils.ServiceUtils;
import com.itsafe.phone.utils.SmsUtils;
import com.itsafe.phone.view.SettingCenterItem;

public class AToolActivity extends Activity {

    private SettingCenterItem mSci_phonequery;
    private SettingCenterItem mSci_servicequery;
    private SettingCenterItem mSci_smsbackup;
    private SettingCenterItem mSci_smsrecovery;
    private NumberProgressBar mPb;
    private SettingCenterItem mSci_applock;
    private SettingCenterItem mSci_dogthread;
    private SettingCenterItem mSci_dogaccessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initEvent();

        initData();
    }

    private void initData() {
        // 初始化看门狗accessibility版
        mSci_dogaccessibility.setToggleOn(ServiceUtils.isServiceRunning(
                getApplicationContext(),
                "com.itsafe.phone.service.MyAccessibilityService"));
        mSci_dogthread.setToggleOn(ServiceUtils.isServiceRunning(
                getApplicationContext(),
                "com.itsafe.phone.service.WatchDogService"));
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
                    case R.id.sci_atool_smsbackup:
                        System.out.println("短信的备份");
                        //用户看到备份的进度
                        smsBackup();
                        break;
                    case R.id.sci_atool_smsrecovery:
                        System.out.println("短信的还原");
                        smsRecovery();
                        break;
                    case R.id.sci_atool_applock:
                        //程序锁
                        Intent applock = new Intent(AToolActivity.this, AppLockActivity.class);
                        startActivity(applock);
                        break;
                    case R.id.sci_atool_watchdog2:
                        if (isOpen) {
                            //启动线程
                            Intent service = new Intent(getApplicationContext(), com.itsafe.phone.service.WatchDogService.class);
                            startService(service);
                        } else {
                            //关闭服务
                            Intent service = new Intent(getApplicationContext(), com.itsafe.phone.service.WatchDogService.class);
                            stopService(service);
                        }
                        break;
                }
            }
        };

        mSci_phonequery.setOnToggleChangedListener(listener);
        mSci_servicequery.setOnToggleChangedListener(listener);
        mSci_smsbackup.setOnToggleChangedListener(listener);
        mSci_smsrecovery.setOnToggleChangedListener(listener);
        mSci_applock.setOnToggleChangedListener(listener);
        mSci_dogthread.setOnToggleChangedListener(listener);
    }

    private void smsRecovery() {
        //progressDialog
        ProgressDialog pd = new ProgressDialog(this);
        //水平进度条
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        SmsUtils.smsRecovery(this, pd);
    }



    private void smsBackup() {
        //progressDialog
        //final ProgressDialog pd = new ProgressDialog(this);
        //水平进度条
        //pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        SmsUtils.smsBackup(this, new SmsUtils.SmsBackRestoreListener() {
            @Override
            public void show() {
                //pd.show();
                mPb.setVisibility(View.VISIBLE);
            }

            @Override
            public void dismiss() {
               // pd.dismiss();
                mPb.setVisibility(View.GONE);
            }

            @Override
            public void setMax(int max) {
                //pd.setMax(max);
                mPb.setMax(max);
            }

            @Override
            public void setProgress(int currentProgress) {
                //pd.setProgress(currentProgress);
                mPb.setProgress(currentProgress);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_atool);
        mSci_phonequery = (SettingCenterItem) findViewById(R.id.sci_atool_mobilephone);
        mSci_servicequery = (SettingCenterItem) findViewById(R.id.sci_atool_servicephone);

        mSci_smsbackup = (SettingCenterItem) findViewById(R.id.sci_atool_smsbackup);
        mSci_smsrecovery = (SettingCenterItem) findViewById(R.id.sci_atool_smsrecovery);

        mPb = (NumberProgressBar) findViewById(R.id.number_progress_bar);
        mPb.setVisibility(View.GONE);

        mSci_applock = (SettingCenterItem) findViewById(R.id.sci_atool_applock);
        mSci_dogaccessibility = (SettingCenterItem) findViewById(R.id.sci_atool_watchdog1);
        mSci_dogthread = (SettingCenterItem) findViewById(R.id.sci_atool_watchdog2);

    }
}

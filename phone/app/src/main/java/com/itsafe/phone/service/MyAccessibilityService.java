package com.itsafe.phone.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;

import com.itsafe.phone.activity.EnterPassLockActivity;
import com.itsafe.phone.dao.LockedDao;

/**
 * 看门狗监控
 */
public class MyAccessibilityService extends AccessibilityService {

    private ShuRenReceiver mShuRenReceiver;

    private String mShuren;

    private LockedDao mLockedDao;

    public MyAccessibilityService() {
    }


    private class ShuRenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mShuren = intent.getStringExtra("shuren");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册熟人的广播
        mShuRenReceiver = new ShuRenReceiver();
        IntentFilter filter = new IntentFilter("itheima.shuren");
        registerReceiver(mShuRenReceiver, filter);
        mLockedDao = new LockedDao(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册
        unregisterReceiver(mShuRenReceiver);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packName = event.getPackageName() + "";

            //判断是否加锁
            if (mLockedDao.isLocked(packName)) {
                //判断是否是熟人
                if (packName.equals(mShuren)) {
                    //放行
                } else {
                    //加锁的拦截
                    //弹出界面 输入密码
                    Intent intent = new Intent(getApplicationContext(), EnterPassLockActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("packname", packName);
                    startActivity(intent);
                    //密码正确 访问
                    //密码错误 继续拦截
                }

            } else {
                //未加锁的放行
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

}

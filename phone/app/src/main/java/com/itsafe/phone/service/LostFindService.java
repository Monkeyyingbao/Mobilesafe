package com.itsafe.phone.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 手机防盗服务
 */
public class LostFindService extends Service {
    public LostFindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //第一次初始化
        super.onCreate();
        System.out.println("防盗服务开启");
    }

    @Override
    public void onDestroy() {
        //服务销毁
        super.onDestroy();
        System.out.println("防盗服务关闭");
    }
}

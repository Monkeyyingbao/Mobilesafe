package com.itsafe.mobile.rocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RocketService extends Service {

    private RocketToast mRocketToast;

    public RocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //开启小火箭
        mRocketToast = new RocketToast(this);
        mRocketToast.show();//显示小火箭
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除小火箭
        mRocketToast.hiden();
    }
}

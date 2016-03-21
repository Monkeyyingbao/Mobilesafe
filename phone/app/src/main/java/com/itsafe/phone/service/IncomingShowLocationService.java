package com.itsafe.phone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.itsafe.phone.dao.AddressDao;
import com.itsafe.phone.view.MyToast;

public class IncomingShowLocationService extends Service {

    private TelephonyManager mTM;
    private PhoneStateListener mListener;
    private MyToast mToast;
    private OutCallReceiver mReceiver;

    public IncomingShowLocationService() {
    }

    private class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取外拨号码
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            System.out.println("number" + number);
            //查询归属地
            String location = AddressDao.getLocation(number);
            mToast.show(location);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        System.out.println("show location start");
        //注册来电状态监听
        registPhoneState();

        //注册外拨电话广播接收
        registOutCall();


        mToast = new MyToast(getApplicationContext());
        super.onCreate();
    }

    private void registOutCall() {
        //初始化外拨电话
        mReceiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver, filter);
    }

    private void registPhoneState() {
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                //监听状态
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE://停止
                        mToast.hiden();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃状态
                        //显示归属地
                        showLocation(incomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                        //mToast.hiden();
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        //监听电话状态
        mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void showLocation(String incomingNumber) {
        //1.获取归属地
        String location = AddressDao.getLocation(incomingNumber);
        //2.toast显示归属地
        //Toast.makeText(IncomingShowLocationService.this, location, Toast.LENGTH_LONG).show();
        mToast.show(location);
    }

    @Override
    public void onDestroy() {
        System.out.println("show location stop");
        mTM.listen(mListener,PhoneStateListener.LISTEN_NONE);
        //取消外拨电话的注册
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}

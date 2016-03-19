package com.itsafe.phone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.itsafe.phone.dao.BlackDao;
import com.itsafe.phone.db.BlackDB;

import java.lang.reflect.InvocationTargetException;

public class BlackService extends Service {

    private SmsReceiver mSmsReceiver;
    private BlackDao mBlackDao;
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mListener;

    public BlackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //短信的拦截
    private class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容
            Object[] smsDatas = (Object[]) intent.getExtras().get("pdus");
            for (Object o : smsDatas) {
                //获取短信
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
                //短信的号码
                String address = smsMessage.getDisplayOriginatingAddress();
                //判断是否是短信拦截
                int mode = mBlackDao.getMode(address);
                if ((mode & BlackDB.SMS_MODE) != 0) {
                    //拦截短信
                    abortBroadcast();//拦截短信
                }
            }
        }
    }
    @Override
    public void onCreate() {

        mBlackDao = new BlackDao(getApplicationContext());
        //服务的创建
        System.out.println("打开服务");
        //短信的拦截
        registSmsintercept();
        //电话的拦截
        registTelintercept();
        super.onCreate();
    }


    private void registTelintercept() {
        //电话拦截
        //监听电话状态
        //电话管理器
        mTelephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        mListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                //监听电话状态的改变
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING://响铃状态
                        phoneIntercept(incomingNumber);
                }
                super.onCallStateChanged(state, incomingNumber);
            }

            private void phoneIntercept(String incomingNumber) {
                //判断是否是黑名单号码
                int mode = mBlackDao.getMode(incomingNumber);
                if ((mode & BlackDB.PHONE_MODE) != 0) {
                    //电话拦截
                    //endCall();
                }
            }
        };
        mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);//监听电话的状态
    }

    private void endCall() throws InvocationTargetException, IllegalAccessException {
        System.out.println("endCall......");
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

    }


    /**
     * 注册短信的拦截
     */
    private void registSmsintercept() {
        mSmsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //设置级别为最高
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mSmsReceiver, filter);//注册短信拦截
    }

    @Override
    public void onDestroy() {
        //服务的关闭
        System.out.println("关闭服务");
        //取消短信拦截
        unregisterReceiver(mSmsReceiver);
        //取消电话监听
        mTelephonyManager.listen(mListener,PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }
}

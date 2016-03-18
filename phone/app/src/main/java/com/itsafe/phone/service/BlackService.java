package com.itsafe.phone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.itsafe.phone.dao.BlackDao;
import com.itsafe.phone.db.BlackDB;

public class BlackService extends Service {

    private SmsReceiver mSmsReceiver;
    private BlackDao mBlackDao;

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
        mSmsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSmsReceiver, filter);//注册短信拦截
        //设置级别为最高
        filter.setPriority(Integer.MAX_VALUE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //服务的关闭
        System.out.println("关闭服务");
        //取消短信拦截
        unregisterReceiver(mSmsReceiver);
        super.onDestroy();
    }
}

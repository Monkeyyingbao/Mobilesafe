package com.itsafe.phone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.itsafe.phone.service.LostFindService;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //系统启动完成

        //1.检测sim卡是否变更
        //获取当前的sim卡和保存的sim卡比较
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currentSerialNum = tm.getSimSerialNumber();
        if (!currentSerialNum.equals(SPUtils.getString(context, StrUtils.SIM_SERIAL_NUM, ""))) {
            //sim卡不一致
            //发送短信给安全号码
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SPUtils.getString(context, StrUtils.SAFENUMBER,"110"),null,"i am xin xiaotou",null,null);

        }
        //2.启动防盗服务
        if (SPUtils.getBoolean(context, StrUtils.BOOTCOMPLETE, false)) {
            //启动服务
            //开启防盗服务
            Intent service = new Intent(context, LostFindService.class);
            context.startService(service);
        }
    }
}

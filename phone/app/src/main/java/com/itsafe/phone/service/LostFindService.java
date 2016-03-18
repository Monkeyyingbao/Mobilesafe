package com.itsafe.phone.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.itsafe.phone.R;
import com.itsafe.phone.receiver.MyDeviceAdminReceiver;

/**
 * 手机防盗服务
 */
public class LostFindService extends Service {

    private SmsReceiver mSmsReceiver;
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;

    public LostFindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //短信的广播接受者
    private class SmsReceiver extends BroadcastReceiver {
        boolean isPlaying = false;//音乐是否播放的标记
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容
            Object[] smsDatas = (Object[]) intent.getExtras().get("pdus");
            for (Object o : smsDatas) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
                String body = smsMessage.getDisplayMessageBody();
                System.out.println(body);
                //根据短信内容进行拦截
                if (body.equals("*#music*#")) {
                    if (!isPlaying) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.qqqg);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                //音乐播完
                                isPlaying = false;
                            }
                        });
                        mediaPlayer.start();//开始播放
                        System.out.println("播放报警音乐");
                        isPlaying = true;
                    }
                    //停止广播向下传递
                    abortBroadcast();
                }else if (body.equals("*#gps*#")) {
                    System.out.println("定位信息");
                    abortBroadcast();
                }else if (body.equals("*#wipedata*#")) {
                    mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    System.out.println("远程清理数据");
                    abortBroadcast();
                }else if (body.equals("*#lockscreen*#")) {
                    System.out.println("远程锁屏");
                    //重置密码
                    mDPM.resetPassword("110", 0);
                    //锁屏
                    mDPM.lockNow();
                    abortBroadcast();
                }
            }
        }
    }
    @Override
    public void onCreate() {
        //第一次初始化
        super.onCreate();
        //注册短信的拦截广播
        mSmsReceiver = new SmsReceiver();
        IntentFilter Intent = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //优先级
        Intent.setPriority(Integer.MAX_VALUE);
        registerReceiver(mSmsReceiver, Intent);
        System.out.println("防盗服务开启");

        //初始化设备管理器的对象
        mDeviceAdminSample = new ComponentName(this, MyDeviceAdminReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public void onDestroy() {
        //服务销毁
        //取消注册
        unregisterReceiver(mSmsReceiver);
        super.onDestroy();
        System.out.println("防盗服务关闭");
    }
}

package com.itsafe.phone.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.itsafe.phone.activity.EnterPassLockActivity;
import com.itsafe.phone.dao.LockedDao;

import java.util.List;

/**
 * 看门狗线程版监控
 */
public class WatchDogService extends Service {

    private ActivityManager mAm;
    private List<String> mAllLockedAppPackName;

    public WatchDogService() {
    }

    private ShuRenReceiver mShuRenReceiver;

    private String mShuren;

    private LockedDao mLockedDao;

    private class ShuRenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mShuren = intent.getStringExtra("shuren");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isRunning;

    @Override
    public void onCreate() {
        super.onCreate();
        //注册熟人的广播
        mShuRenReceiver = new ShuRenReceiver();
        IntentFilter filter = new IntentFilter("itheima.shuren");
        registerReceiver(mShuRenReceiver, filter);
        mLockedDao = new LockedDao(getApplicationContext());

        mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        mAllLockedAppPackName = mLockedDao.getAllLockedAppPackName();

        //启动监控线程
        startDog();
    }

    private void startDog() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                isRunning = true;
                List<ActivityManager.RunningTaskInfo> runningTasks = null;
                ActivityManager.RunningTaskInfo runningTaskInfo = null;
                String packName = null;
                while (isRunning) {
                    //监控任务栈
                    runningTasks = mAm.getRunningTasks(1);
                    //最新打开的任务栈
                    runningTaskInfo = runningTasks.get(0);
                    packName = runningTaskInfo.topActivity.getPackageName();

                    //判断是否加锁
                    if(mAllLockedAppPackName.contains(packName)){//if (mLockedDao.isLocked(packName)) {//内存的优化
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

                    SystemClock.sleep(200);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册
        unregisterReceiver(mShuRenReceiver);
        isRunning = false;
    }
}

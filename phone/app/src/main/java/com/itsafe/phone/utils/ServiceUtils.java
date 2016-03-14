package com.itsafe.phone.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 服务的工具类
 * Created by Hello World on 2016/3/14.
 */
public class ServiceUtils {
    public static boolean isServiceRunning(Context context,String serviceName) {
        //ActivityManager可以获取系统的动态信息,
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取系统中所有运行的服务
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo:runningServices) {
            if (runningServiceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}

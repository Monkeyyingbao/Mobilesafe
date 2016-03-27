package com.itsafe.phone.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.itsafe.phone.domain.AppInfoBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取进程相关的信息
 * Created by Hello World on 2016/3/27.
 */
public class TaskInfoUtils {

    /**
     * @return 可用内存
     */
    public static long getAvailMen(Context context) {
        //ActivityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);//把内存信息写到outInfo对象中了
        return outInfo.availMem;
    }

    /**
     * @return 总内存的大小
     */
    public static long getTotalMen() {
        //ActivityManager
        //  总内存的信息  /porc/meminfo文件
        File file = new File("/proc/meminfo");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = reader.readLine();
            String size = line.substring(line.indexOf(':') + 1, line.length() - 2).trim();
            long totalMem = Long.parseLong(size) * 1024;// 把kb 转成 byte
            return totalMem;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param context 上下文
     * @return 所有运行中的进程信息
     */
    public static List<AppInfoBean> getAllRunningAppInfos(Context context) {
        List<AppInfoBean> mAppInfoBeans = new ArrayList<>();
        //1.获取ActivityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取运行中的进程信息
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.取信息封装
        AppInfoBean bean = null;
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            bean = new AppInfoBean();
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            //占用的内存
            bean.setMemSize(processMemoryInfo[0].getTotalPrivateDirty() * 1024);
            bean.setPackName(runningAppProcessInfo.processName);

            try {
                AppInfoUtils.getAppInfo(context, bean);
                //正常添加
                mAppInfoBeans.add(bean);
            } catch (PackageManager.NameNotFoundException e) {
                //没有名字的进程过滤掉
                e.printStackTrace();
            }
        }
        return mAppInfoBeans;
    }
}

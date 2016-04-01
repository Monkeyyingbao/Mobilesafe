package com.itsafe.phone.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.itsafe.phone.domain.AppInfoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 软件管家的工具类 获取所有软件信息 sd卡可用内存 手机可用内存
 * Created by Hello World on 2016/3/24.
 */
public class AppInfoUtils {

    /**
     * @return 手机总内存
     */
    public static long getPhoneTotalMem() {
        File dataDirectory = Environment.getDataDirectory();
        return dataDirectory.getTotalSpace();
    }

    /**
     * @return 手机可用内存
     */
    public static long getPhoneAvailMem() {
        File dataDirectory = Environment.getDataDirectory();
        return dataDirectory.getFreeSpace();
    }

    /**
     * @return sd卡总内存
     */
    public static long getSDcardTotalMem() {
        File dataDirectory = Environment.getExternalStorageDirectory();
        return dataDirectory.getTotalSpace();
    }

    /**
     * @return sd卡可用内存
     */
    public static long getSDcardAvailMem() {
        File dataDirectory = Environment.getExternalStorageDirectory();
        return dataDirectory.getFreeSpace();
    }

    /**
     *
     * @param context
     * @param bean 一定要先设置包名,就可以吧其他属性封装
     * @throws PackageManager.NameNotFoundException
     */
    public static void getAppInfo(Context context,AppInfoBean bean) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = pm.getApplicationInfo(bean.getPackName(),0);

        //图标
        bean.setIcon(applicationInfo.loadIcon(pm));
        //名字
        bean.setAppName(applicationInfo.loadLabel(pm) + "");

        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            //是系统app
            bean.setIsSystem(true);
        }
        if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
            //安装在sd卡中
            bean.setIsSD(true);
        }

        //路径
        bean.setSourceDir(applicationInfo.sourceDir);

        //安装文件的大小
        bean.setSize(new File(applicationInfo.sourceDir).length());
    }

    /**
     * @return 获取所有安装的app信息
     */
    public static List<AppInfoBean> getAllInstalledAppInfos(Context context) {
        List<AppInfoBean> datas = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);

        AppInfoBean bean = null;
        for (ApplicationInfo applicationInfo : installedApplications) {
            //组织数据
            bean = new AppInfoBean();

            bean.setPackName(applicationInfo.packageName);

            try {
                getAppInfo(context, bean);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            bean.setUid(applicationInfo.uid);

            datas.add(bean);
        }

        return datas;
    }

    //获取总app个数
    public static int getAllInstalledAppNumber(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        return installedPackages.size();
    }


}

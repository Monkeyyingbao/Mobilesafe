package com.itsafe.phone.domain;

import android.graphics.drawable.Drawable;

/**
 * app的基本信息封装
 * Created by Hello World on 2016/3/24.
 */
public class AppInfoBean {
    private Drawable icon;//图标
    private String appName;//app名字
    private boolean isSystem;//是否是系统软件
    private boolean isSD;//是否安装在sd卡中
    private String packName;//app包名
    private long size;//占用的大小
    private String sourceDir;//安装路径

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setIsSystem(boolean isSystem) {
        this.isSystem = isSystem;
    }

    public boolean isSD() {
        return isSD;
    }

    public void setIsSD(boolean isSD) {
        this.isSD = isSD;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }
}
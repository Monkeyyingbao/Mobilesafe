package com.itsafe.phone;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

/**
 * 一个apk对应唯一一个application,在所有功能执行之之前先运行
 * Created by Hello World on 2016/4/2.
 */
public class MyApplication extends Application {

    private void writeExceptionMessage2File(String messsage, File file) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
            out.println(messsage);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //所有功能执行之前执行
        //监控任务异常的状态
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                StringBuilder message = new StringBuilder();

                //反射求出手机机型的信息
                //1. class
                Class type = Build.class;
                //2. 属性
                for (Field field : type.getDeclaredFields()) {
                    try {
                        Object value = field.get(null);
                        message.append(field.getName() + ":" + value + "\n");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }


                //捕获任何线程抛出的异常
                System.out.println(thread.getName() + "出现了异常:" + ex);
                //异常保存,把错误信息保存到sdcard中
                message.append(ex.toString());
                writeExceptionMessage2File(message.toString(), new File("/sdcard/mobilesafeerror.txt"));
                //增强用户体验

                //崩溃前的遗言 : 重生
                Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName());
                startActivity(launchIntentForPackage);
                //挂掉
                android.os.Process.killProcess(Process.myPid());
            }
        });

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

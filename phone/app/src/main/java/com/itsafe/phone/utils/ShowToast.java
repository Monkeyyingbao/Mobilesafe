package com.itsafe.phone.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 吐司的封装
 * Created by Hello World on 2016/3/13.
 */
public class ShowToast {
    public static void show(final String msg, final Activity context) {
        //        //判断是否是主线程
        //        if (Thread.currentThread().getName().equals("main")) {
        //            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        //        } else {
        //            //子线程
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //自动判断是否是主线程
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

            }
        });
    }
}

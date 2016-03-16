package com.itsafe.mobile.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class RemoveActivity extends Activity {

    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mDeviceAdminSample = new ComponentName(this, MyDeviceAdminReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        //        lockScreen(null);
        //        取消激活
        mDPM.removeActiveAdmin(mDeviceAdminSample);
        //卸载
        Intent intent = new Intent("android.intent.action.DELETE");
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * 一键锁屏的功能
     *
     * @param view
     */
    public void lockScreen(View view) {
        //判断是否激活
        boolean adminActive = mDPM.isAdminActive(mDeviceAdminSample);
        if (adminActive) {
            mDPM.lockNow();//锁屏
            finish();//退出
        } else {
            //Toast.makeText(MainActivity.this, "请先点击激活按钮", Toast.LENGTH_SHORT).show();
            jihuo(null);
        }
    }

    /**
     * 打开激活设备管理器界面
     *
     * @param view
     */
    public void jihuo(View view) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启激活设备管理器");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean adminActive = mDPM.isAdminActive(mDeviceAdminSample);
        if (adminActive) {
            mDPM.lockNow();//锁屏
            finish();//退出
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}


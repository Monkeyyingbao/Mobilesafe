package com.itsafe.phone;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itsafe.phone.receiver.MyDeviceAdminReceiver;
import com.itsafe.phone.service.LostFindService;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.ServiceUtils;
import com.itsafe.phone.utils.ShowToast;
import com.itsafe.phone.utils.StrUtils;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox mCb_isopenlostfind;
    private TextView mTv_showState;
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setup4);
        mCb_isopenlostfind = (CheckBox) findViewById(R.id.cb_setup4_isopenlostfind);
        mTv_showState = (TextView) findViewById(R.id.tv_setup4_showstate);

        mDeviceAdminSample = new ComponentName(this, MyDeviceAdminReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

    }

    @Override
    protected void initData() {
        //初始化复选框的状态
        if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.itsafe.phone.service.LostFindService")) {
            mCb_isopenlostfind.setChecked(true);
        } else {
            mCb_isopenlostfind.setChecked(false);

        }
        super.initData();
    }

    @Override
    protected void initEvent() {
        //添加复选框状态改变时间
        mCb_isopenlostfind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //状态变化相应结果
                //1.文字的变化
                if (isChecked) {
                    //判断是否激活了设备管理器
                    if (!mDPM.isAdminActive(mDeviceAdminSample)) {
                        //没有激活
                        //打开激活界面
                        activeDevice();
                    } else {
                        //开启防盗服务
                        Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                        startService(service);
                        //开启
                        mTv_showState.setText("手机防盗已经开启");
                        //状态不应该保存到sp中,动态判断服务是否运行ActivityManager
                    }

                } else {
                    //关闭
                    mTv_showState.setText("手机防盗已经关闭");
                    //关闭放到服务
                    Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                    stopService(service);
                }
            }
        });
    }

    private void activeDevice() {
        Intent intent =  new  Intent ( DevicePolicyManager. ACTION_ADD_DEVICE_ADMIN );
        intent . putExtra ( DevicePolicyManager . EXTRA_DEVICE_ADMIN , mDeviceAdminSample );
        intent . putExtra ( DevicePolicyManager . EXTRA_ADD_EXPLANATION , "开启激活设备管理器");
        startActivityForResult ( intent , 1 );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mDPM.isAdminActive(mDeviceAdminSample)) {
            //开启防盗服务
            Intent service = new Intent(Setup4Activity.this, LostFindService.class);
            startService(service);
            //开启
            mTv_showState.setText("手机防盗已经开启");
            //状态不应该保存到sp中,动态判断服务是否运行ActivityManager
        } else {
            //取消勾选复选框
            mCb_isopenlostfind.setChecked(false);
            ShowToast.show("先激活才能启动防盗服务",Setup4Activity.this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void startNext() {
        //完成设置
        if (!mCb_isopenlostfind.isChecked()) {
            //提示必须勾选
            ShowToast.show("必须勾选开启防盗", Setup4Activity.this);
        } else {
            //完成设置,保存设置状态
            SPUtils.putBoolean(getApplicationContext(), StrUtils.ISSETUPFINISH,true);
            //添加系统启动自动开启防盗服务
            SPUtils.putBoolean(getApplication(),StrUtils.BOOTCOMPLETE,true);
            startPage(LostFindActivity.class);
        }
    }

    @Override
    protected void startPrev() {
        startPage(Setup3Activity.class);
    }
}

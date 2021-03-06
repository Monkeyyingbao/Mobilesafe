package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itsafe.phone.R;
import com.itsafe.phone.service.BlackService;
import com.itsafe.phone.service.IncomingShowLocationService;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.ServiceUtils;
import com.itsafe.phone.utils.StrUtils;
import com.itsafe.phone.view.SettingCenterItem;
import com.itsafe.phone.view.ShowLocationStyleDialog;

/**
 *
 */
public class SettingCenterActivity extends Activity {

    private RelativeLayout mRl_autouptade;
    private ImageView mIv_autoupdate;
    private RelativeLayout mRl_blackintercept;
    private ImageView mIv_blackintercept;
    private SettingCenterItem mSci_autoupdate;
    private SettingCenterItem mSci_blackintercept;
    private SettingCenterItem mSci_showLocation;
    private SettingCenterItem mSci_locationStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initData() {

        //初始化样式名
        mSci_locationStyle.setText("归属地样式("+ShowLocationStyleDialog.styleNames[SPUtils.getInt(getApplicationContext(),StrUtils.LOCATIONSTYLEINDEX,0)]+")");
        //初始化黑名单拦截状态
        {
           /* if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.itsafe.phone.service.BlackService")) {
                //开关打开状态
                mIv_blackintercept.setImageResource(R.drawable.on);
            } else {
                //开关关闭状态
                mIv_blackintercept.setImageResource(R.drawable.off);
            }*/
            mSci_blackintercept.setToggleOn(ServiceUtils.isServiceRunning(getApplicationContext(), "com.itsafe.phone.service.BlackService"));
        }
        //初始化自动更新的状态
        {
            boolean autoUpdate = SPUtils.getBoolean(getApplicationContext(), StrUtils.AUTO_CHECK_VERSION, false);
            mSci_autoupdate.setToggleOn(autoUpdate);
        }
        //设置来电归属地的初始化状态
        mSci_showLocation.setToggleOn(ServiceUtils.isServiceRunning(getApplicationContext(), "com.itsafe.phone.service.IncomingShowLocationService"));
    }

    private void initEvent() {
        SettingCenterItem.OnToggleChangedListener onToggleChangedListener = new SettingCenterItem.OnToggleChangedListener() {
            @Override
            public void onToggleChanged(View view, boolean isOpen) {
                //判断view类型
                switch (view.getId()) {
                    case R.id.sci_settingcenter_autoupdate:
                        //自动更新设置
                        //更改并保存状态
                        SPUtils.putBoolean(getApplicationContext(), StrUtils.AUTO_CHECK_VERSION, isOpen);
                        break;
                    case R.id.sci_settingcenter_blackintercept:
                        //黑名单拦截
                        if (isOpen) {
                            //打开服务
                            Intent intent = new Intent(SettingCenterActivity.this, BlackService.class);
                            startService(intent);
                        } else {
                            //关闭服务
                            Intent intent = new Intent(SettingCenterActivity.this, BlackService.class);
                            stopService(intent);
                        }
                        break;
                    case R.id.sci_settingcenter_showlocation:
                        if (isOpen) {
                            //打开服务
                            Intent intent = new Intent(SettingCenterActivity.this, IncomingShowLocationService.class);
                            startService(intent);
                        } else {
                            //关闭服务
                            Intent intent = new Intent(SettingCenterActivity.this, IncomingShowLocationService.class);
                            stopService(intent);
                        }
                        break;
                    case R.id.sci_settingcenter_locationstyle:
                        //归属地的样式
                        //弹出对话框
                        ShowLocationStyleDialog dialog = new ShowLocationStyleDialog(SettingCenterActivity.this,mSci_locationStyle);
                        dialog.show();
                        break;
                }
            }
        };
        //自动更新添加事件
        mSci_autoupdate.setOnToggleChangedListener(onToggleChangedListener);
        //黑名单拦截
        mSci_blackintercept.setOnToggleChangedListener(onToggleChangedListener);

        mSci_showLocation.setOnToggleChangedListener(onToggleChangedListener);

        mSci_locationStyle.setOnToggleChangedListener(onToggleChangedListener);

       /* //黑名单拦截事件
        mRl_blackintercept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //黑名单拦截服务的控制
                if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.itsafe.phone.service.BlackService")) {
                    //关闭服务
                    Intent intent = new Intent(SettingCenterActivity.this, BlackService.class);
                    stopService(intent);
                    //开关的关闭
                    mIv_blackintercept.setImageResource(R.drawable.off);
                } else {
                    //打开服务
                    Intent intent = new Intent(SettingCenterActivity.this, BlackService.class);
                    startService(intent);
                    //开关打开
                    mIv_blackintercept.setImageResource(R.drawable.on);
                }
            }
        });*/
        /*//自动更新
        mRl_autouptade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取出数据
                boolean autoUpdate = SPUtils.getBoolean(getApplicationContext(), StrUtils.AUTO_CHECK_VERSION, false);
                //更改并保存状态
                SPUtils.putBoolean(getApplicationContext(), StrUtils.AUTO_CHECK_VERSION, !autoUpdate);
                //改变开关的图标
                mIv_autoupdate.setImageResource(!autoUpdate ? R.drawable.on : R.drawable.off);
            }
        });*/
    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);
        mSci_autoupdate = (SettingCenterItem) findViewById(R.id.sci_settingcenter_autoupdate);
        mSci_blackintercept = (SettingCenterItem) findViewById(R.id.sci_settingcenter_blackintercept);
        mSci_showLocation = (SettingCenterItem) findViewById(R.id.sci_settingcenter_showlocation);
        mSci_locationStyle = (SettingCenterItem) findViewById(R.id.sci_settingcenter_locationstyle);
    }
}

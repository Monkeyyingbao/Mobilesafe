package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itsafe.phone.R;
import com.itsafe.phone.service.BlackService;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.ServiceUtils;
import com.itsafe.phone.utils.StrUtils;

/**
 *
 */
public class SettingCenterActivity extends Activity {

    private RelativeLayout mRl_autouptade;
    private ImageView mIv_autoupdate;
    private RelativeLayout mRl_blackintercept;
    private ImageView mIv_blackintercept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        //初始化黑名单拦截状态
        {
            if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.itsafe.phone.service.BlackService")) {
                //开关打开状态
                mIv_blackintercept.setImageResource(R.drawable.on);
            } else {
                //开关关闭状态
                mIv_blackintercept.setImageResource(R.drawable.off);
            }
        }
        //初始化自动更新的状态
        {
            boolean autoUpdate = SPUtils.getBoolean(getApplicationContext(), StrUtils.AUTO_CHECK_VERSION, false);
            //初始化开关的图标
            mIv_autoupdate.setImageResource(autoUpdate ? R.drawable.on : R.drawable.off);
        }
    }

    private void initEvent() {

        //黑名单拦截事件
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
        });
        //自动更新
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
        });
    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);

        mRl_autouptade = (RelativeLayout) findViewById(R.id.rl_settingcenter_autoupdate);
        mIv_autoupdate = (ImageView) findViewById(R.id.iv_settingcenter_autoupdate);
        mRl_blackintercept = (RelativeLayout) findViewById(R.id.rl_settingcenter_balckintercept);
        mIv_blackintercept = (ImageView) findViewById(R.id.iv_settingcenter_blackintercept);

    }
}

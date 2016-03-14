package com.itsafe.phone;

import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itsafe.phone.service.LostFindService;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.ServiceUtils;
import com.itsafe.phone.utils.ShowToast;
import com.itsafe.phone.utils.StrUtils;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox mCb_isopenlostfind;
    private TextView mTv_showState;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setup4);
        mCb_isopenlostfind = (CheckBox) findViewById(R.id.cb_setup4_isopenlostfind);
        mTv_showState = (TextView) findViewById(R.id.tv_setup4_showstate);

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
                    //开启
                    mTv_showState.setText("手机防盗已经开启");
                    //开启防盗服务
                    Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                    startService(service);
                    //状态不应该保存到sp中,动态判断服务是否运行ActivityManager
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

    @Override
    protected void startNext() {
        //完成设置
        if (!mCb_isopenlostfind.isChecked()) {
            //提示必须勾选
            ShowToast.show("必须勾选开启防盗", Setup4Activity.this);
        } else {
            //完成设置,保存设置状态
            SPUtils.putBoolean(getApplicationContext(), StrUtils.ISSETUPFINISH,true);
            startPage(LostFindActivity.class);
        }
    }

    @Override
    protected void startPrev() {
        startPage(Setup3Activity.class);
    }
}

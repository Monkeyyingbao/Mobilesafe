package com.itsafe.phone.activity;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.itsafe.phone.R;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;

public class Setup2Activity extends BaseSetupActivity {

    private ImageView mIv_setup2_simbind_icon;

    @Override
    protected void initData() {
        //初始化sim卡状态
        String simSerNum = SPUtils.getString(Setup2Activity.this, StrUtils.SIM_SERIAL_NUM, null);
        if (TextUtils.isEmpty(simSerNum)) {
            //未绑定
            mIv_setup2_simbind_icon.setImageResource(R.mipmap.unlock);
        } else {
            //已绑定
            mIv_setup2_simbind_icon.setImageResource(R.mipmap.lock);

        }
        super.initData();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setup2);
        //SIM卡的是否绑定图标
        mIv_setup2_simbind_icon = (ImageView) findViewById(R.id.iv_setup2_simbind_icon);
    }

    @Override
    protected void startNext() {
        //判断是否绑定,否则不让下一步,提示绑定
        String simSerNum = SPUtils.getString(Setup2Activity.this, StrUtils.SIM_SERIAL_NUM, null);
        if (TextUtils.isEmpty(simSerNum)) {
            //不让下一步,提示绑定
            Toast.makeText(Setup2Activity.this, "请先绑定sim卡", Toast.LENGTH_SHORT).show();
        } else {
            startPage(Setup3Activity.class);
        }
    }

    @Override
    protected void startPrev() {
        startPage(Setup1Activity.class);
    }

    /**
     * 绑定sim卡点击事件
     *
     * @param view
     */
    public void bindSIM(View view) {
        //判断是否绑定
        String simSerNum = SPUtils.getString(Setup2Activity.this, StrUtils.SIM_SERIAL_NUM, null);
        if (TextUtils.isEmpty(simSerNum)) {
            //绑定sim卡 取出sim卡信息 保存到sp中
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String simSerialNumber = tm.getSimSerialNumber();
            //保存
            SPUtils.putString(Setup2Activity.this, StrUtils.SIM_SERIAL_NUM, simSerialNumber);
            //修改图标
            mIv_setup2_simbind_icon.setImageResource(R.mipmap.lock);
        } else {
            //解绑
            SPUtils.putString(Setup2Activity.this, StrUtils.SIM_SERIAL_NUM, "");
            //修改图标
            mIv_setup2_simbind_icon.setImageResource(R.mipmap.unlock);
        }
    }
}

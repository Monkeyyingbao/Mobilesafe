package com.itsafe.phone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.dao.AddressDao;
import com.itsafe.phone.utils.ShowToast;

public class PhoneLocationActivity extends Activity {

    private EditText mEt_phone;
    private TextView mTv_showlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    private void initEvent() {
        //监听文本的变化事件
        mEt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //文本变化前
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //文本变化
                //动态查询
                query(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //文本变化后
            }
        });
    }

    public void query(View view) {
        //查询
        String phone = mEt_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ShowToast.show("号码不能为空",this);
            //设置抖动效果
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            mEt_phone.startAnimation(shake);
            //震动效果
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{300,200,300,200},3);
            return;
        }
        //判断是移动号还是固定好
        String location = null;
        try {
            location = AddressDao.getLocation(phone);
            //显示
            mTv_showlocation.setText("归属地:\n" + location);
        } catch (Exception e) {
        }
    }

    private void initView() {
        setContentView(R.layout.activity_phone_location);
        mEt_phone = (EditText) findViewById(R.id.et_phonelocation_phone);
        mTv_showlocation = (TextView) findViewById(R.id.tv_phonelocation_showlocation);
    }
}

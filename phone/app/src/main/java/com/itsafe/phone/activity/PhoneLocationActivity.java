package com.itsafe.phone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
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
        String location = AddressDao.getLocation(phone);
        //显示
        mTv_showlocation.setText("归属地:\n"+location);
    }

    private void initView() {
        setContentView(R.layout.activity_phone_location);
        mEt_phone = (EditText) findViewById(R.id.et_phonelocation_phone);
        mTv_showlocation = (TextView) findViewById(R.id.tv_phonelocation_showlocation);
    }
}

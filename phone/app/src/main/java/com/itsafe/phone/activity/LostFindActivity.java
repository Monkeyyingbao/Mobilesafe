package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;

/**
 * 手机防盗界面
 */
public class LostFindActivity extends Activity {

    private TextView mTv_safenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否设置向导完成
        if (SPUtils.getBoolean(getApplicationContext(), StrUtils.ISSETUPFINISH, false)) {
            //设置向导完成
            initView();
            initData();
        } else {
            //设置向导未完成
            //进入第一个设置向导界面
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //关闭自己
            finish();
        }

    }

    public void enterSetup1(View v){
        //进入第一个设置向导界面
        Intent setup1 = new Intent(this,Setup1Activity.class);
        startActivity(setup1);
        finish();
    }
    private void initData() {
        // 显示安全号码
        mTv_safenumber.setText(SPUtils.getString(getApplicationContext(), StrUtils.SAFENUMBER, ""));
    }

    private void initView() {
        //初始化界面
        setContentView(R.layout.activity_lost_find);
        mTv_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
    }
}

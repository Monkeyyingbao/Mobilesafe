package com.itsafe.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 手机防盗界面
 */
public class LostFindActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否设置向导完成
        if (SPUtils.getBoolean(getApplicationContext(), StrUtils.ISSETUPFINISH, false)) {
            //设置向导完成
            initView();
        } else {
            //设置向导未完成
            //进入第一个设置向导界面
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //关闭自己
            finish();
        }

    }

    private void initView() {
        //初始化界面
        setContentView(R.layout.activity_lost_find);
    }
}

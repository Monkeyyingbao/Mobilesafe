package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itsafe.phone.R;

/**
 * 看门狗的输入密码界面
 */
public class EnterPassLockActivity extends Activity {

    private String mPackname;
    private EditText mEt_pass;
    private ImageView mIv_icon;
    private HomeReceiver mHomeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initHomeReceiver();
    }

    private class HomeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断时间的类型
            if (intent.getAction().contains(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                //home键的广播
                loadMain();
            }

        }
    }
    private void initHomeReceiver() {
        //注册home键的广播
        mHomeReceiver = new HomeReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消home键的广播注册
        unregisterReceiver(mHomeReceiver);
    }

    private void initData() {
        mPackname = getIntent().getStringExtra("packname");

        PackageManager pm = getPackageManager();
        try {
            mIv_icon.setImageDrawable(pm.getApplicationIcon(mPackname));//图标
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void enter(View view) {
        String password = mEt_pass.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(EnterPassLockActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.equals("1")) {
            //告诉看门狗是熟人,放行
            Intent intent = new Intent("itheima.shuren");
            intent.putExtra("shuren", mPackname);
            sendBroadcast(intent);
            //关闭
            finish();

        } else {
            //密码错误
            Toast.makeText(EnterPassLockActivity.this, "坏人", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void initView() {
        setContentView(R.layout.activity_enter_pass);

        mEt_pass = (EditText) findViewById(R.id.et_lock_pass_password);
        mIv_icon = (ImageView) findViewById(R.id.iv_lock_pass_appicon);

    }

    //监听home键 广播


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //监听按键的信息
        //监听返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //返回到主界面
            loadMain();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void loadMain() {
        //手机的主界面
        Intent main = new Intent("android.intent.action.MAIN");
        main.addCategory("android.intent.category.HOME");
        main.addCategory("android.intent.category.DEFAULT");
        main.addCategory("android.intent.category.MONKEY");
        startActivity(main);//进入手机的主界面

        //关闭自己
        finish();
    }
}

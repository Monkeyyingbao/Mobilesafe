package com.itsafe.mobile.rocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv_rocket = (ImageView) findViewById(R.id.iv_rocket);

       /* //动画
        AnimationDrawable ad = (AnimationDrawable) iv_rocket.getBackground();
        ad.start();//开始动画*/
    }

    public void startRocket(View view) {
        //启动小火箭
        //启动服务
        Intent service = new Intent(this, RocketService.class);
        startService(service);
        //关闭activity
        finish();
    }
}

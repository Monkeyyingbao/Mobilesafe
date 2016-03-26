package com.itsafe.mobile.rocket;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class SmokeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_smoke);

        //烟柱
        ImageView iv_smoket = (ImageView) findViewById(R.id.iv_rocket_smoket);
        //烟的底部
        ImageView iv_smokem = (ImageView) findViewById(R.id.iv_rocket_smokem);

        AlphaAnimation aa = new AlphaAnimation(1.0f,0.0f);//从不透明到透明
        aa.setDuration(2000);

        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //动画播完关闭activity
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ScaleAnimation sa = new ScaleAnimation(1.0f,1.0f,0,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1.0f);
        sa.setDuration(1000);
        //烟柱 比例 alpha
        AnimationSet as = new AnimationSet(false);
        as.addAnimation(aa);
        as.addAnimation(sa);

        iv_smoket.startAnimation(as);
        //底部 alpha
        iv_smokem.startAnimation(aa);


       /*
        new Thread() {
            @Override
            public void run() {
                super.run();
                SystemClock.sleep(3000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }.start();*/
    }
}

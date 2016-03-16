package com.itsafe.mobile.popupwindowdemo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void popupWindow(View view) {
        View contentView = View.inflate( getApplicationContext(), R.layout.view_popup,null);
        //-2 wrap_content
        PopupWindow mPw = new PopupWindow(contentView, 130, -2);
        //获取焦点
        mPw.setFocusable(true);
        //窗体外部点击消失
        mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置透明背景
        mPw.setOutsideTouchable(true);
        //显示的位置
        mPw.showAsDropDown(view);
        //播放动画
        Animation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(500);
        contentView.startAnimation(animation);
    }
}

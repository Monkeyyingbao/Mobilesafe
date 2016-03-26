package com.itsafe.mobile.rocket;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;



/**
 * 自定义Toast
 * Created by Hello World on 2016/3/21.
 */
public class RocketToast implements View.OnTouchListener {

    private final WindowManager mWM;
    private final WindowManager.LayoutParams mParams;
    private View mView;
    private RocketService mContext;
    private TextView mTv_mess;
    private float mDownX;
    private float mDownY;

    public RocketToast(RocketService context) {
        mContext = context;
        //1.WindowManager
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //2.参数Params
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        //mParams.windowAnimations = com.android.internal.R.style.Animation_Toast;
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                /*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;//坐标原点为左上角
        //3.view
    }


    public void show() {
        hiden();
        mView = View.inflate(mContext, R.layout.rocket_view, null);
        //帧动画播放
        AnimationDrawable background = (AnimationDrawable) mView.getBackground();
        background.start();//开始动画
        mView.setOnTouchListener(this);
        mWM.addView(mView, mParams);
    }

    public void hiden() {
        if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }
            mView = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //触摸事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                System.out.println("mToast按下");
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE://移动
                System.out.println("mToast移动");
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                float dx = moveX - mDownX;
                float dy = moveY - mDownY;

                //改变Toast参数
                mParams.x += dx;
                mParams.y += dy;

                //x y新的坐标
                //判断越界
                if (mParams.x < 0) {
                    mParams.x = 0;
                } else if (mParams.x > mWM.getDefaultDisplay().getWidth() - mView.getWidth()){
                    mParams.x = mWM.getDefaultDisplay().getWidth() - mView.getWidth();
                }

                if (mParams.y < 0) {
                    mParams.y = 0;
                }else if (mParams.y > mWM.getDefaultDisplay().getHeight() - mView.getHeight()) {
                    mParams.y = mWM.getDefaultDisplay().getHeight() - mView.getHeight();
                }

                //改变view的位置
                mWM.updateViewLayout(mView,mParams);
                //变为新的起始位置
                mDownX = moveX;
                mDownY = moveY;
                break;
            case MotionEvent.ACTION_UP://松开
                System.out.println("mToast松开");
                //是否发射
                //判断位置
                int height = mWM.getDefaultDisplay().getHeight();
                if (mParams.y > height * 5/8) {
                    //超过高度的5/8发射
                    //启动烟的activity
                    smokeView();

                    //移动小火箭
                    rocketMove();
                }
                break;
        }
        return true;//自己消费掉
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //更新小火箭的位置
                    mWM.updateViewLayout(mView, mParams);
                    break;
                case 2:
                    mContext.stopSelf();
                    break;
            }
        }
    };

    private void rocketMove() {
        //从当前位置往上跑
        //中间 x坐标=(屏幕宽度的一半 - 小火箭宽度的一半)
        mParams.x = Math.round((mWM.getDefaultDisplay().getWidth() - mView.getWidth()) / 2);

        //耗时的操作,阻塞主线程
        final int time = 2000 / mParams.y;
        new Thread(){
            @Override
            public void run() {
                for (int y = mParams.y; y >= 0; y -= 5) {
                    //改变mParams.y的坐标
                    mParams.y = y;

                    //休眠
                    SystemClock.sleep(time);
                    //更新view
                    mHandler.obtainMessage(1).sendToTarget();
                }
                //发射完毕
                //关闭小火箭
                //关闭服务
                mHandler.obtainMessage(2).sendToTarget();

            }
        }.start();

    }

    private void smokeView() {
        //广播或者服务中启动activity
        Intent smoke = new Intent(mContext,SmokeActivity.class);
        //设置属性 独立启动
        smoke.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(smoke);
    }
}

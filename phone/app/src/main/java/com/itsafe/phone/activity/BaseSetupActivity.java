package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.itsafe.phone.R;

/**
 * Created by Hello World on 2016/3/10.
 */
public abstract class BaseSetupActivity extends Activity {

    private GestureDetector mGD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initEvent();

        initData();

        initGesture();
    }

    /**
     * 添加手势识别器
     */
    protected void initGesture() {
        mGD = new GestureDetector(new MyOnGestureListener(){
            /**
             *
             * @param e1 按下的点
             * @param e2 松开的点
             * @param velocityX 速度x轴
             * @param velocityY 速度y轴
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //判断是否是x轴方向滑动
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
                    //横向滑动
                    //判断速度
                    if (Math.abs(velocityX) > 50) {
                        //判断方向
                        if (velocityX > 0) {
                            Log.i("velocityX", "onFling: 从左往右划");
                            prevPage(null);
                        } else {
                            Log.i("velocityX", "onFling: 从右往左划");
                            nextPage(null);
                        }
                    }
                }
                return true;
            }
        });
    }
    //注册滑动事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGD != null) {
            mGD.onTouchEvent(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    //开启界面的接口方法
    public void startPage(Class type) {
        Intent intent = new Intent(this, type);
        startActivity(intent);
        finish();//跳转过去后关闭自己
    }
    protected abstract void startNext();
    protected abstract void startPrev();

    /**
     * 跳转到下一个界面的点击事件
     * @param view
     */
    public void nextPage(View view) {
        //下一个界面
        startNext();
        //位移动画
        nextPageAnimation();
    }

    /**
     * 返回上一个界面的点击事件
     * @param view
     */
    public void prevPage(View view) {
        //上一个界面
        startPrev();
        //位移动画
        prevPageAnimation();
    }
    //上个界面位移动画
    protected void prevPageAnimation() {
        //上一个页面切换的动画
        overridePendingTransition(R.animator.prev_enter_anim, R.animator.prev_exit_anim);
    }
    //下个界面位移动画
    private void nextPageAnimation() {
        //下一个页面切换的动画
        overridePendingTransition(R.animator.next_enter_anim, R.animator.next_exit_anim);

    }
    /**
     * 子类复写此方法完成数据的初始化
     */
    protected void initData() {

    }

    /**
     * 子类复写此方法完成事件的初始化
     */
    protected void initEvent() {

    }
    /**
     * 子类复写此方法完成界面的初始化
     */
    protected void initView() {

    }
    private class MyOnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}


package com.itsafe.phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Hello World on 2016/3/10.
 */
public abstract class BaseSetupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initEvent();

        initData();
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
}

package com.itsafe.phone.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itsafe.phone.R;

/**
 * 程序锁界面头布局的封装view
 * Created by Hello World on 2016/3/30.
 */
public class AppLockHeadView extends RelativeLayout{

    private View mRootView;
    private TextView mTv_unlock;
    private TextView mTv_lock;

    public AppLockHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

        initEvent();
    }


    private OnLockChangeListener mOnLockChangeListener;

    public void setOnLockChangeListener(OnLockChangeListener onLockChangeListener) {
        this.mOnLockChangeListener = onLockChangeListener;
    }

    /**
     * 按钮的回调接口
     */
    public interface OnLockChangeListener {
        /**
         *
         * @param isLocked
         *  true 已加锁
         *  false 未加锁
         */
        void onLockChange(boolean isLocked);
    }

    private void initEvent() {
        OnClickListener listener = new OnClickListener() {
            boolean isLock = false;
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_applock_lock://加锁
                        isLock = true;
                        mTv_lock.setBackgroundResource(R.mipmap.tab_left_pressed);
                        mTv_unlock.setBackgroundResource(R.mipmap.tab_right_default);
                        mTv_lock.setTextColor(Color.WHITE);
                        mTv_unlock.setTextColor(Color.GRAY);
                        break;

                    case R.id.tv_applock_unlock://未加锁
                        isLock = false;
                        mTv_lock.setBackgroundResource(R.mipmap.tab_left_default);
                        mTv_unlock.setBackgroundResource(R.mipmap.tab_right_pressed);
                        mTv_lock.setTextColor(Color.GRAY);
                        mTv_unlock.setTextColor(Color.WHITE);
                        break;
                }

                //处理回调,把数据状态给调用者
                if (mOnLockChangeListener != null) {
                    mOnLockChangeListener.onLockChange(isLock);
                }
            }
        };

        mTv_lock.setOnClickListener(listener);
        mTv_unlock.setOnClickListener(listener);
    }

    private void initView() {
        //把布局转成view添加到容器中
        mRootView = View.inflate(getContext(), R.layout.view_applockhead, this);
        mTv_unlock = (TextView) mRootView.findViewById(R.id.tv_applock_unlock);
        mTv_lock = (TextView) mRootView.findViewById(R.id.tv_applock_lock);

    }


    public AppLockHeadView(Context context) {
        this(context,null);
    }
}

package com.itsafe.phone.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;

/**
 * 自定义Toast
 * Created by Hello World on 2016/3/21.
 */
public class MyToast implements View.OnTouchListener {

    private final WindowManager mWM;
    private final WindowManager.LayoutParams mParams;
    private View mView;
    private Context mContext;
    private TextView mTv_mess;
    private float mDownX;
    private float mDownY;

    public MyToast(Context context) {
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
        mParams.x = SPUtils.getInt(mContext,StrUtils.TOASTX,0);
        mParams.y = SPUtils.getInt(mContext,StrUtils.TOASTY,0);
        //3.view
    }

    /**
     *
     * @param styleIndex 归属地样式的标记
     */
    private void setBackGroundStyle(int styleIndex) {
        mView.setBackgroundResource(ShowLocationStyleDialog.bgColors[styleIndex]);
    }

    public void show(String location) {
        mView = View.inflate(mContext, R.layout.sys_toast, null);
        //设置背景的样式
        setBackGroundStyle(SPUtils.getInt(mContext,StrUtils.LOCATIONSTYLEINDEX,0));
        mTv_mess = (TextView) mView.findViewById(R.id.tv_toast_text);
        mTv_mess.setText(location);
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
                //保存toast的位置
                SPUtils.putInt(mContext, StrUtils.TOASTX,mParams.x);
                SPUtils.putInt(mContext, StrUtils.TOASTY,mParams.y);
                break;
        }
        return true;//自己消费掉
    }
}

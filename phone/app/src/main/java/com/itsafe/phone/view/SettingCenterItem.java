package com.itsafe.phone.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itsafe.phone.R;

/**
 * Created by Hello World on 2016/3/19.
 */
public class SettingCenterItem extends RelativeLayout {

    private TextView mTv_desc;
    private View mRootView;
    private boolean isOpen = false;//开关关闭
    private ImageView mIv_toggle;

    /**
     * 代码中实例化
     *
     * @param context
     */
    public SettingCenterItem(Context context) {
        this(context, null);
    }

    /**
     * 布局文件中实例化调用
     *
     * @param context
     * @param attrs
     */
    public SettingCenterItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

        initData(attrs);

        initEvent();
    }

    //接口暴露给需要调用该功能的程序员
    public interface OnToggleChangedListener {
        void onToggleChanged(View view,boolean isOpen);
    }

    private OnToggleChangedListener mOnToggleChangedListener;

    public void setOnToggleChangedListener(OnToggleChangedListener listener) {
        this.mOnToggleChangedListener = listener;
    }

    public void setToggleOn(boolean isOpen){
        //保存当前的状态
        this.isOpen = isOpen;
        if (isOpen) {
            //设置mIv_toggle为打开的图片
            mIv_toggle.setImageResource(R.drawable.on);
        } else {
            mIv_toggle.setImageResource(R.drawable.off);
        }
    }

    private void initEvent() {
        //给mRootView添加点击事件
        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换开关的状态
                isOpen = !isOpen;
                if (isOpen) {
                    //设置mIv_toggle为打开的图片
                    mIv_toggle.setImageResource(R.drawable.on);
                } else {
                    mIv_toggle.setImageResource(R.drawable.off);
                }

                //接口
                if (mOnToggleChangedListener != null) {
                    //设置了监听器
                    mOnToggleChangedListener.onToggleChanged(SettingCenterItem.this,isOpen);
                }
            }
        });
    }

    private void initData(AttributeSet attrs) {
        //取出属性
        String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itsafe.phone", "desc");
        //取背景选择器
        String bgtype = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itsafe.phone", "bgselector");
        //设置属性
        mTv_desc.setText(desc);
        //根据bgtype设置背景选择器
        switch (Integer.parseInt(bgtype)) {
            case 0://first
                mRootView.setBackgroundResource(R.drawable.iv_first_selector);
                break;
            case 1://middle
                mRootView.setBackgroundResource(R.drawable.iv_middle_selector);
                break;
            case 2://last
                mRootView.setBackgroundResource(R.drawable.iv_last_selector);
                break;
        }

        boolean isdisabletoggle = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/com.itsafe.phone", "isdisabletoggle", false);
        if (isdisabletoggle) {
            //不显示
            mIv_toggle.setVisibility(GONE);
        }
    }


    private void initView() {
        //自定义控件
        //view添加到RelativeLayout中
        mRootView = View.inflate(getContext(), R.layout.view_setting_item_view, this);
        mTv_desc = (TextView) mRootView.findViewById(R.id.tv_view_settingview_item_desc);
        mIv_toggle = (ImageView) mRootView.findViewById(R.id.iv_view_settingview_item_toggle);
    }
}

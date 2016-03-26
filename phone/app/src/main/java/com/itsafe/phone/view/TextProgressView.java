package com.itsafe.phone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itsafe.phone.R;

/**
 * Created by Hello World on 2016/3/26.
 */
public class TextProgressView extends RelativeLayout {

    private TextView mTv_mess;
    private ProgressBar mPb_progress;

    public TextProgressView(Context context) {
        this(context, null);
    }

    public TextProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.view_messageprogress, this);

        mTv_mess = (TextView) rootView.findViewById(R.id.tv_progresstext_message);
        mPb_progress = (ProgressBar) rootView.findViewById(R.id.pb_progresstext_progresssale);
        mPb_progress.setProgress(100);
    }

    /**
     * 设置进度条的进度
     * @param progressScale 百分比
     */
    public void setProgress(double progressScale) {
        mPb_progress.setProgress((int)Math.round(100 * progressScale));
    }

    /**
     *
     * @param message 显示的值
     */
    public void setMessage(String  message) {
        mTv_mess.setText(message);
    }
}

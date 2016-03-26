package com.itsafe.phone.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;

/**
 * Created by Hello World on 2016/3/22.
 */
public class ShowLocationStyleDialog extends Dialog{

    private ListView mLv_datas;

    public static final String[] styleNames = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    public static final int[] bgColors = new int[]{
            R.drawable.call_locate_white,
            R.drawable.call_locate_orange,
            R.drawable.call_locate_blue,
            R.drawable.call_locate_gray,
            R.drawable.call_locate_green};

    private SettingCenterItem sci_style = null;
    public ShowLocationStyleDialog(Context context,SettingCenterItem sci_style) {
        this(context, R.style.LocationStyle);//0代表对话框默认的样式
        this.sci_style = sci_style;
    }

    public ShowLocationStyleDialog(Context context, int themeResId) {
        super(context, themeResId);
        //设置样式
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //设置对其方式 底部对其
        layoutParams.gravity = Gravity.BOTTOM;
        //设置参数
        window.setAttributes(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initView();

        initEvent();
    }

    private void initEvent() {
        //listview点击事件
        mLv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //保存点击的位置
                SPUtils.putInt(getContext(),StrUtils.LOCATIONSTYLEINDEX,position);
                //设置文本
                sci_style.setText("归属地样式("+styleNames[position]+")");
                //关闭对话框
                dismiss();
            }
        });
    }

    private void initView() {
        setContentView(R.layout.dialog_locationstyle_view);
        mLv_datas = (ListView) findViewById(R.id.lv_locationstyle_dialog_content);
        MyAdapter adapter = new MyAdapter();
        mLv_datas.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return styleNames.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_locationstyledialog_lv,null);
            }

            View v_stylebg = convertView.findViewById(R.id.v_dialogstyle_bg);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_dialogstyle_name);
            ImageView iv_select = (ImageView) convertView.findViewById(R.id.iv_dialogstyle_select);

            tv_name.setText(styleNames[position]);//样式名字
            v_stylebg.setBackgroundResource(bgColors[position]);//样式背景

            //样式的标记(整数) 保存SP中
            if (position == SPUtils.getInt(getContext(), StrUtils.LOCATIONSTYLEINDEX, 0)) {
                iv_select.setVisibility(View.VISIBLE);
            } else {
                iv_select.setVisibility(View.GONE);
            }
            return convertView;
        }
    }
}

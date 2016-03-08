package com.itsafe.phone;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {

    private ImageView mIv_logo;//logo旋转图标
    private ImageView mIv_setting;//设置按钮
    private GridView mGv_tools;

    public static final String[] names = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "病毒查杀", "缓存清理", "高级工具"};
    public static final String[] desc = {"手机丢失好找", "防骚扰防监听", "方便管理软件", "保持手机通畅", "注意流量超标", "手机安全保障", "手机快步如飞", "特性处理更好"};
    public static final int[] icons = {R.drawable.sjfd, R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
            R.drawable.sjsd, R.drawable.hcql, R.drawable.szzx};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        //初始化界面
        initView();
        //开始动画
        startAnimation();
        //设置数据
        initData();
        //初始化事件
        initEvent();
    }

    private void initEvent() {

    }

    private void initData() {
        mGv_tools.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
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
            View view = View.inflate(getApplicationContext(), R.layout.item_home_gv, null);
            TextView tv_name_gv = (TextView) view.findViewById(R.id.name_home_item);
            TextView tv_desc_gv = (TextView) view.findViewById(R.id.desc_home_item);
            ImageView iv_icon_gv = (ImageView) view.findViewById(R.id.gv_icon);
            //设置标题
            tv_name_gv.setText(names[position]);
            //设置描述
            tv_desc_gv.setText(desc[position]);
            //设置图片
            iv_icon_gv.setImageResource(icons[position]);
            return view;
        }
    }

    /**
     * 开始动画
     */
    private void startAnimation() {
        //给logo设置属性动画
        ObjectAnimator oa = ObjectAnimator.ofFloat(mIv_logo, "rotationY", 0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360);
        oa.setDuration(2000);//完成时间
        oa.setRepeatCount(ValueAnimator.INFINITE);//动画重复次数-1无限
        oa.start();
    }

    private void initView() {
        //        找到关心控件
        //旋转图标
        mIv_logo = (ImageView) findViewById(R.id.iv_home_logo);
        //设置按钮
        mIv_setting = (ImageView) findViewById(R.id.iv_home_setting);
        mGv_tools = (GridView) findViewById(R.id.gv_home_tools);
    }
}

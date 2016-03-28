package com.itsafe.phone.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itsafe.phone.R;
import com.itsafe.phone.domain.AppInfoBean;
import com.itsafe.phone.utils.AppInfoUtils;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;
import com.itsafe.phone.utils.TaskInfoUtils;
import com.itsafe.phone.view.TextProgressView;

import java.util.List;
import java.util.Vector;

/**
 * 进程管家
 */
public class TaskManagerActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView mLv_datas;
    private TextProgressView mTpv_memory_info;
    private TextProgressView mTpv_processnumber_info;
    private LinearLayout mLl_loading;
    private TextView mTv_tag;

    //用户进程数据
    private List<AppInfoBean> userAppInfoBeans = new Vector<>();
    //系统进程数据
    private List<AppInfoBean> sysAppInfoBeans = new Vector<>();
    private long mTotalMen;
    private long mAvailMen;
    private int mAllInstalledAppNumber;
    private MyAdapter mAdapter;
    private ActivityManager mAm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        //lv 的item点击事件
        mLv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == userAppInfoBeans.size() + 1) {
                    //点击的是标签
                    return;
                }
                //点击条目 改变checkbox的状态
                //获取条目数据
                AppInfoBean bean = (AppInfoBean) mLv_datas.getItemAtPosition(position);
                //改变数据状态
                bean.setIsChecked(!bean.isChecked());

                if (bean.getPackName().equals(getPackageName())) {
                    //是自己就改为未选中状态
                    bean.setIsChecked(false);
                }
                //更新界面
                mAdapter.notifyDataSetChanged();
            }
        });

        //lv的滑动事件
        mLv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //判断第一个显示数据的位置
                if (firstVisibleItem >= userAppInfoBeans.size() + 1) {
                    //系统的标签
                    mTv_tag.setText("系统软件(" + sysAppInfoBeans.size() + ")");
                } else {
                    //用户的标签
                    mTv_tag.setText("用户软件(" + userAppInfoBeans.size() + ")");
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOADING://正在加载数据
                    mLl_loading.setVisibility(View.VISIBLE);
                    mLv_datas.setVisibility(View.GONE);
                    mTv_tag.setVisibility(View.GONE);
                    break;
                case FINISH://加载数据完成
                    mLl_loading.setVisibility(View.GONE);
                    mLv_datas.setVisibility(View.VISIBLE);
                    mTv_tag.setVisibility(View.VISIBLE);

                    //显示数据
                    //1.显示进程个数的信息
                    int taskNumber = (userAppInfoBeans.size() + sysAppInfoBeans.size());
                    mTpv_processnumber_info.setMessage("运行中的进程:" + taskNumber);
                    mTpv_processnumber_info.setProgress(taskNumber * 1.0 / mAllInstalledAppNumber);

                    //2.内存的使用
                    mTpv_memory_info.setMessage("可用内存/总内存:" + Formatter.formatFileSize(getApplicationContext(), mAvailMen) + "/" + Formatter.formatFileSize(getApplicationContext(), mTotalMen));
                    mTpv_memory_info.setProgress((mTotalMen - mAvailMen) * 1.0 / mTotalMen);

                    //lv的数据更新
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        //子线程获取数据
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1.发送正在加载数据的消息
                mHandler.obtainMessage(LOADING).sendToTarget();

                //2.获取数据
                List<AppInfoBean> allRunningAppInfos = TaskInfoUtils.getAllRunningAppInfos(getApplicationContext());

                //分类数据
                for (AppInfoBean bean : allRunningAppInfos) {
                    if (bean.isSystem()) {
                        sysAppInfoBeans.add(bean);//系统的
                    } else {
                        userAppInfoBeans.add(bean);//用户的
                    }
                }

                //内存信息
                mTotalMen = TaskInfoUtils.getTotalMen();
                mAvailMen = TaskInfoUtils.getAvailMen(getApplicationContext());

                //总app个数
                mAllInstalledAppNumber = AppInfoUtils.getAllInstalledAppNumber(getApplicationContext());

                SystemClock.sleep(1000);
                //3.发送数据加载完成的消息
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //数据的个数
            return userAppInfoBeans.size() + 1 + sysAppInfoBeans.size() + 1;
        }

        @Override
        public AppInfoBean getItem(int position) {
            AppInfoBean bean = null;
            //根据position 获取bean
            if (position <= userAppInfoBeans.size()) {
                bean = userAppInfoBeans.get(position - 1);
            } else {
                //系统
                bean = sysAppInfoBeans.get(position - userAppInfoBeans.size() - 2);
            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {
            //view
            //两个标签
            if (position == 0) {
                //用户app的标签
                TextView tv_usertag = new TextView(getApplicationContext());
                tv_usertag.setBackgroundColor(Color.GRAY);
                tv_usertag.setTextColor(Color.WHITE);
                tv_usertag.setText("用户软件(" + userAppInfoBeans.size() + ")");
                return tv_usertag;

            } else if (position == userAppInfoBeans.size() + 1) {
                //系统的app标签
                TextView tv_usertag = new TextView(getApplicationContext());
                tv_usertag.setBackgroundColor(Color.GRAY);
                tv_usertag.setTextColor(Color.WHITE);
                tv_usertag.setText("系统软件(" + sysAppInfoBeans.size() + ")");
                return tv_usertag;

            } else {
                //自定义view
                ViewHolder viewHolder = null;
                if (convertView == null || convertView instanceof TextView) {
                    //创建view
                    convertView = View.inflate(getApplicationContext(), R.layout.item_taskmanager_lv, null);
                    viewHolder = new ViewHolder();

                    //app名字
                    viewHolder.tv_appName = (TextView) convertView.findViewById(R.id.tv_taskmanager_lv_appname);
                    //app内存信息
                    viewHolder.tv_appMemorySize = (TextView) convertView.findViewById(R.id.tv_taskmanager_lv_appmemorysize);
                    //图标
                    viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_taskmanager_lv_icon);
                    //是否勾选
                    viewHolder.cb_checked = (CheckBox) convertView.findViewById(R.id.cb_taskmanager_isselete);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                //显示信息
                final AppInfoBean bean = getItem(position);

                viewHolder.tv_appName.setText(bean.getAppName());
                viewHolder.tv_appMemorySize.setText(Formatter.formatFileSize(getApplicationContext(), bean.getMemSize()));
                viewHolder.iv_icon.setImageDrawable(bean.getIcon());
                //复选框

                viewHolder.cb_checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //记录勾选的状态
                        bean.setIsChecked(isChecked);
                    }
                });

                //设置复选框的初始状态
                viewHolder.cb_checked.setChecked(bean.isChecked());

                //设置自己隐藏
                if (bean.getPackName().equals(getPackageName())) {
                    //自己
                    viewHolder.cb_checked.setVisibility(View.GONE);
                } else {
                    //不是自己必须显示
                    viewHolder.cb_checked.setVisibility(View.VISIBLE);
                }
                return convertView;
            }
        }
    }

    private class ViewHolder {
        TextView tv_appName;
        CheckBox cb_checked;
        TextView tv_appMemorySize;
        ImageView iv_icon;
    }

    private void initView() {
        setContentView(R.layout.activity_taskmanager);
        //显示进程信息
        mLv_datas = (ListView) findViewById(R.id.lv_taskmanager_viewdatas);
        mAdapter = new MyAdapter();
        mLv_datas.setAdapter(mAdapter);

        //内存信息
        mTpv_memory_info = (TextProgressView) findViewById(R.id.tpv_taskmanager_memory_mess);
        //进程信息
        mTpv_processnumber_info = (TextProgressView) findViewById(R.id.tpv_taskmanager_processnumber_mess);
        //显示加载数据进度
        mLl_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);
        //lv的tag
        mTv_tag = (TextView) findViewById(R.id.tv_taskmanager_lvtag);

        mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    }

    /**
     * 全选按钮点击事件
     *
     * @param view
     */
    public void selectAll(View view) {
        //user
        for (AppInfoBean bean : userAppInfoBeans) {
            //过滤自己
            if (bean.getPackName().equals(getPackageName())) {
                continue;
            }
            //设置反选状态
            bean.setIsChecked(true);
        }

        //system
        for (AppInfoBean bean : sysAppInfoBeans) {

            //设置反选状态
            bean.setIsChecked(true);
        }

        //通知UI
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 反选按钮点击事件
     *
     * @param view
     */
    public void selectNone(View view) {
        //user
        for (AppInfoBean bean : userAppInfoBeans) {
            //过滤自己
            if (bean.getPackName().equals(getPackageName())) {
                continue;
            }
            //设置反选状态
            bean.setIsChecked(!bean.isChecked());
        }

        //system
        for (AppInfoBean bean : sysAppInfoBeans) {

            //设置反选状态
            bean.setIsChecked(!bean.isChecked());
        }
        //通知UI
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 清理选中的进程
     *
     * @param view
     */
    public void clearTask(View view) {
        long clearSize = 0;//记录清理的总内存大小
        int clearNumber = 0;//记录清理的进程个数

        for (int i = 0; i < userAppInfoBeans.size(); i++) {
            AppInfoBean bean = userAppInfoBeans.get(i);
            if (bean.isChecked()) {
                clearNumber++;
                clearSize += bean.getMemSize();
                //清理
                mAm.killBackgroundProcesses(bean.getPackName());
                //界面效果
                userAppInfoBeans.remove(i--);
            }
        }

        for (int i = 0; i < sysAppInfoBeans.size(); i++) {
            AppInfoBean bean = sysAppInfoBeans.get(i);
            if (bean.isChecked()) {
                clearNumber++;
                clearSize += bean.getMemSize();
                //清理
                mAm.killBackgroundProcesses(bean.getPackName());
                //界面效果
                sysAppInfoBeans.remove(i--);
            }
        }

        //提醒清理工作
        Toast.makeText(getApplicationContext(), "清理了" + clearNumber + "个进程,释放了" + Formatter.formatFileSize(getApplicationContext(), clearSize) + "内存", Toast.LENGTH_SHORT).show();

        //listview界面更新
        mAdapter.notifyDataSetChanged();

        //进程个数和使用内存更新
        //1.显示进程个数的信息
        int taskNumber = (userAppInfoBeans.size() + sysAppInfoBeans.size());
        mTpv_processnumber_info.setMessage("运行中的进程:" + taskNumber);
        mTpv_processnumber_info.setProgress(taskNumber * 1.0 / mAllInstalledAppNumber);

        //2.内存的使用
        mTpv_memory_info.setMessage("可用内存/总内存:" + Formatter.formatFileSize(getApplicationContext(), (mAvailMen + clearSize)) + "/" + Formatter.formatFileSize(getApplicationContext(), mTotalMen));
        mTpv_memory_info.setProgress((mTotalMen - (mAvailMen + clearSize)) * 1.0 / mTotalMen);

        //记录清理时间
        if ((sysAppInfoBeans.size() + userAppInfoBeans.size()) < 3) {
            SPUtils.putLong(getApplicationContext(), StrUtils.CLEARTIME, System.currentTimeMillis());
        }

        //user
        /*for (AppInfoBean bean : userAppInfoBeans) {
            if (bean.isChecked()) {
                //清理
                mAm.killBackgroundProcesses(bean.getPackName());
                //界面效果
                userAppInfoBeans.remove(bean);//并发修改异常
            }
        }

        //system
        for (AppInfoBean bean : sysAppInfoBeans) {
            if (bean.isChecked()) {
                //清理
                mAm.killBackgroundProcesses(bean.getPackName());
                //界面效果
                sysAppInfoBeans.remove(bean);//并发修改异常
            }
        }*/


    }
}

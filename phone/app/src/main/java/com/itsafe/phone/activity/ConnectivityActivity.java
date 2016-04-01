package com.itsafe.phone.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.domain.AppInfoBean;
import com.itsafe.phone.utils.AppInfoUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计的界面
 */
public class ConnectivityActivity extends ListActivity {

    public static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView mLv_showdatas;
    private MyAdapter mAdapter;

    private List<ConnectivityBean> mConnectivityBeans = new ArrayList<>();
    private ConnectivityManager mCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();
    }

    Handler mHandler = new Handler() {

        private ProgressDialog pd;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOADING:
                    //显示对话框
                    pd = new ProgressDialog(ConnectivityActivity.this);
                    pd.setTitle("注意");
                    pd.setMessage("正在读取流量信息");
                    pd.show();
                    break;
                case FINISH://读取完成
                    pd.dismiss();
                    //显示数据
                    mAdapter.notifyDataSetChanged();
                    break;

            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mConnectivityBeans.size();
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
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                //没有缓存view
                viewHolder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.item_connectivity_lv, null);

                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_connectivity_lv_icon);
                viewHolder.tv_rcv = (TextView) convertView.findViewById(R.id.tv_connectivity_lv_rcv);
                viewHolder.tv_snd = (TextView) convertView.findViewById(R.id.tv_connectivity_lv_sed);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_connectivity_lv_type);

                convertView.setTag(viewHolder);
            } else {
                //有复用view
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //显示
            ConnectivityBean connectivityBean = mConnectivityBeans.get(position);
            viewHolder.iv_icon.setImageDrawable(connectivityBean.icon);
            viewHolder.tv_rcv.setText("接收: "+Formatter.formatFileSize(getApplicationContext(),connectivityBean.rcvSize));
            viewHolder.tv_snd.setText("发送: "+Formatter.formatFileSize(getApplicationContext(),connectivityBean.sndSize));
            viewHolder.tv_type.setText(connectivityBean.type);

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_snd;
        TextView tv_rcv;
        ImageView iv_icon;
        TextView tv_type;
    }


    /**
     * 流量的路径
     *
     * @param path 流量的路径
     * @return 流量的大小 byte单位 -1没有流量
     */
    private long readFile(String path) {
        long size = -1;
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line = reader.readLine();
            size = Long.parseLong(line);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1. 发送加载数据的消息
                mHandler.obtainMessage(LOADING).sendToTarget();
                //2. 读取数据
                List<AppInfoBean> allInstalledAppInfos = AppInfoUtils.getAllInstalledAppInfos(getApplicationContext());
                String rcvPath = null;
                String sndPath = null;
                for (AppInfoBean appInfoBean : allInstalledAppInfos) {
                    rcvPath = "/proc/uid_stat/" + appInfoBean.getUid() + "/tcp_rcv";
                    sndPath = "/proc/uid_stat/" + appInfoBean.getUid() + "/tcp_snd";

                    //是否有流量信息
                    long rcvSize = readFile(rcvPath);
                    long sndSize = readFile(sndPath);

                    if (rcvSize == -1 && sndSize == -1) {
                        //没有流量
                    } else {
                        //有流量
                        ConnectivityBean bean = new ConnectivityBean();
                        bean.icon = appInfoBean.getIcon();
                        bean.sndSize = sndSize;
                        bean.rcvSize = rcvSize;
                        bean.type = mCM.getActiveNetworkInfo().getTypeName();//网络类型名字

                        mConnectivityBeans.add(bean);
                    }
                }

                //2. 发送数据加载完成的消息
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private class ConnectivityBean {
        public long rcvSize;//接收大小
        public long sndSize;//发送大小
        public Drawable icon;//图标
        public String type;//类型
    }

    private void initView() {
        mLv_showdatas = getListView();
        mAdapter = new MyAdapter();
        mLv_showdatas.setAdapter(mAdapter);

        mCM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}

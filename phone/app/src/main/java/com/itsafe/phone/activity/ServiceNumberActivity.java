package com.itsafe.phone.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.dao.AddressDao;
import com.itsafe.phone.domain.NumberAndName;
import com.itsafe.phone.domain.ServiceNameAndType;

import java.util.ArrayList;
import java.util.List;


public class ServiceNumberActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ExpandableListView mElv_showmess;

    //保存elv的组数据和每组对应的数据
    //private HashMap<ServiceNameAndType, List<NumberAndName>> mDatas = new HashMap<>();
    private List<ServiceNameAndType> mServiceNameAndTypes = new ArrayList<>();
    private List<List<NumberAndName>> mNumberAndNames = new ArrayList<>();
    private LinearLayout mLl_loading;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        //添加列表点击事件
        mElv_showmess.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //获取当前数据 拨打电话
                String number = mNumberAndNames.get(groupPosition).get(childPosition).getNumber();
                //启动外拨电话的界面
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + number));
                if (ActivityCompat.checkSelfPermission(ServiceNumberActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                startActivity(call);
                return true;
            }
        });
    }

    //handler处理结果
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //处理消息
            switch (msg.what) {
                case LOADING:
                    //正在加载数据
                    mLl_loading.setVisibility(View.VISIBLE);
                    mElv_showmess.setVisibility(View.GONE);
                    break;
                case FINISH:
                    //加载数据完成
                    mLl_loading.setVisibility(View.GONE);
                    mElv_showmess.setVisibility(View.VISIBLE);
                    //刷新数据
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        //子线程取数据
        new Thread(){
            @Override
            public void run() {
                //1.取数据的消息
                mHandler.obtainMessage(LOADING).sendToTarget();
                //2.取数据
                List<ServiceNameAndType> allServiceTypes = AddressDao.getAllServiceTypes();
                //获取组的信息
                mServiceNameAndTypes = allServiceTypes;
                for (ServiceNameAndType serviceNameAndType : allServiceTypes) {
                    List<NumberAndName> numberAndNames = AddressDao.getNumberAndNames(serviceNameAndType);
                    //添加数据
                    //mDatas.put(serviceNameAndType, numberAndNames);
                    mNumberAndNames.add(numberAndNames);

                }
                //3.发送取数据完成的消息
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_service_number);
        mElv_showmess = (ExpandableListView) findViewById(R.id.elv_servicenumber_show);
        mLl_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);
        mAdapter = new MyAdapter();
        mElv_showmess.setAdapter(mAdapter);
        //mElv_showmess. setSelector(new ColorDrawable(Color.BLUE));
    }

    private class MyAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            //多少个组
            return mServiceNameAndTypes.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            //每个组多少数据
            return mNumberAndNames.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public NumberAndName getChild(int groupPosition, int childPosition) {
            return mNumberAndNames.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            //组的显示
            TextView tv = null;
            if (convertView == null) {
                tv = new TextView(getApplicationContext());
                tv.setTextSize(20);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.BLUE);
            } else {
                tv = (TextView) convertView;
            }

            //赋值
            tv.setText(mServiceNameAndTypes.get(groupPosition).getName());
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            //组的子view的显示
            TextView tv = null;
            if (convertView == null) {
                tv = new TextView(getApplicationContext());
                tv.setTextSize(18);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
            } else {
                tv = (TextView) convertView;
            }

            //赋值
            NumberAndName numberAndName = mNumberAndNames.get(groupPosition).get(childPosition);
            tv.setText(numberAndName.getName() + " " + numberAndName.getNumber());
            return tv;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


}

package com.itsafe.phone.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.domain.ContactBean;
import com.itsafe.phone.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示所有好友信息的界面
 */
public abstract class BaseSmsTelFriendsActivity extends ListActivity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_datas;
    private List<ContactBean> mDatas = new ArrayList<>();
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //显示所有的好友
        //1.获取数据
        //2.定义适配器
        //3.给ListView设置适配器
        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        //给listView添加item点击事件
        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击item数据
                //该方法会调用adapter的getItem方法
                ContactBean bean = (ContactBean) lv_datas.getItemAtPosition((int) lv_datas.getItemIdAtPosition(position));
                //保存数据
                Intent data = new Intent();
                //设置安全号码
                data.putExtra(StrUtils.SAFENUMBER, bean.getPhone());
                setResult(1,data );
                //关闭自己
                finish();

            }
        });
    }

    private Handler mHandler = new Handler(){
        ProgressDialog pd;
        @Override
        public void handleMessage(Message msg) {
            //在主线程中执行
            switch (msg.what) {
                case LOADING:
                    lv_datas.setVisibility(View.GONE);
                    //通过对话框来显示加载数据
                    pd = new ProgressDialog(BaseSmsTelFriendsActivity.this);
                    pd.setTitle("注意");
                    pd.setMessage("正在玩命加载数据......");
                    pd.show();
                    break;
                case FINISH:
                    //关闭对话框
                    pd.dismiss();
                    lv_datas.setVisibility(View.VISIBLE);
                    //更新数据
                    mAdapter.notifyDataSetChanged();//通知界面刷新数据
                    break;

            }

        }
    };
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                //1.提醒用户正在加载数据
                mHandler.obtainMessage(LOADING).sendToTarget();
                //2.加载数据
                mDatas = getDatas();
                //3.数据加载完成
                //发送数据加载完成的消息
                //模拟耗时1秒
                SystemClock.sleep(1000);
                mHandler.obtainMessage(FINISH).sendToTarget();

            }
        }.start();
    }

    public abstract List<ContactBean> getDatas();
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public ContactBean getItem(int position) {
            //获取适配器中的数据
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //缓存View
            if (convertView == null) {
                //如果没有缓存
                //把布局转成view
                convertView = View.inflate(getApplicationContext(), R.layout.item_contacts_lv, null);
                holder = new ViewHolder();
                //把子view设置给viewholder
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_contact_name);
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_item_contact_phone);
                //设置标记给convertView
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //获取数据
            ContactBean bean = getItem(position);
            //设置数据
            holder.tv_name.setText(bean.getName());
            holder.tv_phone.setText(bean.getPhone());
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_phone;
    }
    private void initView() {
        lv_datas = getListView();
        mAdapter = new MyAdapter();
        //设置适配器
        lv_datas.setAdapter(mAdapter);
    }
}

package com.itsafe.phone.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.dao.LockedDao;
import com.itsafe.phone.domain.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * 程序锁的基本fragment
 */
public class BaseLockFragment extends Fragment {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private MyAdapter mAdapter;

    private LockedDao mLockedDao;

    private List<AppInfoBean> mDatas = new ArrayList<>();
    private StickyListHeadersListView mLv_showlocks;

    public BaseLockFragment() {
    }
    public void setLockedDao(LockedDao lockedDao) {
        this.mLockedDao = lockedDao;
    }

    private Handler mHandler = new Handler() {

        private ProgressDialog pb;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOADING:
                    mLv_showlocks.setVisibility(View.INVISIBLE);
                    pb = new ProgressDialog(getActivity());
                    pb.setTitle("注意");
                    pb.setMessage("正在玩命加载数据...");
                    pb.show();
                    break;

                case FINISH:
                    mLv_showlocks.setVisibility(View.VISIBLE);
                    if (pb != null && pb.isShowing()) {
                        pb.dismiss();
                        pb = null;
                    }

                    //显示数据
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    //所有安装的app的bean集合
    private List<AppInfoBean> allInstalledAppInfos;
    public void setAllInstalledAppInfos(List<AppInfoBean> allInstalledAppInfos) {
        this.allInstalledAppInfos = allInstalledAppInfos;
    }

    //所有已加锁的app的包名的集合
    private List<String> allLockedPackageName;
    public void setAllLockedPackageName(List<String> allLockedPackageName) {
        this.allLockedPackageName = allLockedPackageName;
    }

    public void initData() {
        //耗时操作
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1. 发送加载数据的消息
                mHandler.obtainMessage(LOADING).sendToTarget();

                //2. 获取数据
                mDatas.clear();//先清空,否则重复添加


                for (AppInfoBean appInfoBean : allInstalledAppInfos) {
                    if ((BaseLockFragment.this instanceof LockedFragment) && allLockedPackageName.contains((appInfoBean.getPackName()))) { //mLockedDao.isLocked(appInfoBean.getPackName()
                        //加锁
                        mDatas.add(appInfoBean);
                    } else if ((BaseLockFragment.this instanceof UnLockedFragment) && !allLockedPackageName.contains((appInfoBean.getPackName()))) { //mLockedDao.isLocked(appInfoBean.getPackName()
                        //未加锁
                        mDatas.add(appInfoBean);
                    } else {
                        //不做处理
                    }
                }

                //发送加载完成的消息
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }


    private class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
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
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_applock_fragment, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_appname = (TextView) convertView.findViewById(R.id.tv_item_applock_frgment_appname);
                viewHolder.iv_appicon = (ImageView) convertView.findViewById(R.id.iv_item_applock_frgment_icon);
                viewHolder.iv_lockOrunlock = (ImageView) convertView.findViewById(R.id.iv_item_applock_frgment_lockorunlock);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //赋值
            final AppInfoBean appInfoBean = mDatas.get(position);
            viewHolder.iv_appicon.setImageDrawable(appInfoBean.getIcon());
            viewHolder.tv_appname.setText(appInfoBean.getAppName());

            if (BaseLockFragment.this instanceof LockedFragment) {
                viewHolder.iv_lockOrunlock.setImageResource(R.mipmap.unlock);
            } else {
                viewHolder.iv_lockOrunlock.setImageResource(R.mipmap.lock);
            }

            final View rootView = convertView;
            //添加加锁或解锁的事件
            viewHolder.iv_lockOrunlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BaseLockFragment.this instanceof LockedFragment) {
                        //解锁的业务
                        mLockedDao.rmeoveLockedPackName(appInfoBean.getPackName());
                        //解锁动画
                        rootView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.unlocked_translate));
                    } else {
                        //加锁的业务
                        mLockedDao.addLockedPackName(appInfoBean.getPackName());
                        //加锁动画
                        rootView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.locked_translate));

                    }
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            //子线程休眠等待动画播放
                            SystemClock.sleep(500);

                            //让主线程更新界面
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //界面的处理
                                    mDatas.remove(appInfoBean);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }.start();

                }
            });

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            TextView tv_tag = new TextView(getActivity());
            tv_tag.setBackgroundColor(Color.GRAY);
            tv_tag.setTextSize(18);
            tv_tag.setTextColor(Color.WHITE);

            //条件
            AppInfoBean bean = mDatas.get(position);
            if (bean.isSystem()) {
                tv_tag.setText("系统软件");
            } else {
                tv_tag.setText("用户软件");

            }
            return tv_tag;
        }

        @Override
        public long getHeaderId(int position) {
            //条件
            AppInfoBean bean = mDatas.get(position);
            if (bean.isSystem()) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    private class ViewHolder {
        ImageView iv_appicon;
        TextView tv_appname;
        ImageView iv_lockOrunlock;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("onAttach" + getClass().getName());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("onActivityCreated" + getClass().getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate" + getClass().getName());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mLv_showlocks = (StickyListHeadersListView) View.inflate(getActivity(), R.layout.fragment_lock_view, null);

        if (mAdapter == null) {
            mAdapter = new MyAdapter();
        }
        //每次都要做适配器
        mLv_showlocks.setAdapter(mAdapter);
        return mLv_showlocks;
    }

    @Override
    public void onStart() {
        super.onStart();

        //初始化数据
        initData();

    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop" + getClass().getName());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy" + getClass().getName());

    }
}

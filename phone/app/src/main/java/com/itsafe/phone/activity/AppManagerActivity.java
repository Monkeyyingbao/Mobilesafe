package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.domain.AppInfoBean;
import com.itsafe.phone.utils.AppInfoUtils;
import com.itsafe.phone.view.TextProgressView;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * 软件管家的界面
 */
public class AppManagerActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;

    //显示app信息
    private StickyListHeadersListView mLv_datas;
    private LinearLayout mLl_loading;
    private List<AppInfoBean> mAllInstalledAppInfos = new Vector<>();
    //系统的App容器
    private List<AppInfoBean> mSystemAllInstalledAppInfos = new ArrayList<>();
    //用户的App容器
    private List<AppInfoBean> mUserAllInstalledAppInfos = new ArrayList<>();
    private long mPhoneAvailMem;
    private long mSDcardAvailMem;
    private MyAdapter mAdapter;
    //private TextView mTv_tag;
    private ProgressBar mPb_progress;
    private long mPhoneTotalMem;
    //手机的内存信息
    private TextProgressView mTvp_rom_mess;
    //sd卡的内存信息
    private TextProgressView mMTvp_sd_mess;
    private long mSDcardTotalMem;
    private PopupWindow mPW;
    private AppInfoBean mClickedAppInfoBean;
    private RemoveAppReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();

        initEvent();

        initPopupWindow();

        //注册广播
        registRemoveAppReceiver();
    }

    private void registRemoveAppReceiver() {
        mReceiver = new RemoveAppReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);

            filter.addDataScheme("package");
            registerReceiver(mReceiver, filter);
    }

    //删除app的监听
    private class RemoveAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //监听到广播后更新数据
            initData(); //用于卸载系统软件
            System.out.println("BroadcastReceiver : app remove......");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁活动时取消注册
        unregisterReceiver(mReceiver);
    }

    private void initPopupWindow() {
        //初始化弹出窗体
        View mPopupViewRoot = View.inflate(getApplicationContext(), R.layout.popupwindow_appmanager_view, null);

        LinearLayout ll_uninstall = (LinearLayout) mPopupViewRoot.findViewById(R.id.ll_appmanager_uninstall);
        LinearLayout ll_start = (LinearLayout) mPopupViewRoot.findViewById(R.id.ll_appmanager_start);
        LinearLayout ll_share = (LinearLayout) mPopupViewRoot.findViewById(R.id.ll_appmanager_share);
        LinearLayout ll_setting = (LinearLayout) mPopupViewRoot.findViewById(R.id.ll_appmanager_setting);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_appmanager_uninstall://卸载
                        uninstall();
                        break;
                    case R.id.ll_appmanager_start://开启
                        startApp();
                        break;
                    case R.id.ll_appmanager_share://分享
                        share();
                        break;
                    case R.id.ll_appmanager_setting://设置
                        setting();
                        break;
                }

                //关闭popupwindow
                mPW.dismiss();
            }
        };

        ll_uninstall.setOnClickListener(listener);
        ll_start.setOnClickListener(listener);
        ll_share.setOnClickListener(listener);
        ll_setting.setOnClickListener(listener);

        mPW = new PopupWindow(mPopupViewRoot, -2, -2);

        //获取焦点 保证里面的控件可以点击
        mPW.setFocusable(true);

        //设置背景
        mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //外部点击消失,要想生效先设置背景
        mPW.setOutsideTouchable(true);

        //动画
        mPW.setAnimationStyle(R.style.PPDialog);

    }

    private void setting() {
        //START {act=android.settings.APPLICATION_DETAILS_SETTINGS dat=package:com.itsafe.phone flg=0x10800000 cmp=com.android.settings/.applications.InstalledAppDetails} from pid 1029
        Intent setting = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        setting.setData(Uri.parse("package:"+mClickedAppInfoBean.getPackName()));
        startActivity(setting);

    }

    private void share() {
        //公众平台
        showShare();

    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.ssdk_oks_share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }

    private void startApp() {
        //根据包名获得启动意图
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(mClickedAppInfoBean.getPackName());
        startActivity(launchIntentForPackage);
    }

    private void uninstall() {
        //卸载
        //判断app
        if (mClickedAppInfoBean.isSystem()) {
            //系统app
            try {
                RootTools.sendShell("mount -o remount rw /system",5000);
                RootTools.sendShell("rm -r " + mClickedAppInfoBean.getSourceDir(), 5000);
                RootTools.sendShell("mount -o remount r /system",5000);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (RootToolsException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

        } else {
            //用户APP
            Intent uninstall = new Intent("android.intent.action.DELETE");
            uninstall.setData(Uri.parse("package:" + mClickedAppInfoBean.getPackName()));
            startActivity(uninstall);
            //刷新界面
        }

    }

    private void initEvent() {

        mLv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //显示popupwindow
                mPW.showAsDropDown(view, 45, -view.getHeight());
                //保留点击的数据
                mClickedAppInfoBean = mAllInstalledAppInfos.get(position);

            }
        });
    /*//给listview添加滑动事件
       mLv_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滑动到系统标签的时候改变tv_tag的内容
                //系统标签第一条数据
                if (firstVisibleItem >= (mUserAllInstalledAppInfos.size() + 1)) {
                    //系统软件的tag
                    //mTv_tag.setText("系统软件(" + mSystemAllInstalledAppInfos.size() + ")");
                } else {
                    //用户app的teg
                    //mTv_tag.setText("用户软件(" + mUserAllInstalledAppInfos.size() + ")");
                }
            }
        });*/
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOADING://正在加载数据
                    mLl_loading.setVisibility(View.VISIBLE);
                    mLv_datas.setVisibility(View.GONE);
                    //mTv_tag.setVisibility(View.GONE);
                    break;
                case FINISH://数据加载完成
                    mLl_loading.setVisibility(View.GONE);
                    mLv_datas.setVisibility(View.VISIBLE);
                    //mTv_tag.setVisibility(View.VISIBLE);

                    //显示数据可用内存 格式化数据
                    mTvp_rom_mess.setMessage("rom的可用内存" + Formatter.formatFileSize(getApplicationContext(), mPhoneAvailMem));

                    //进度比
                    mTvp_rom_mess.setProgress((mPhoneTotalMem - mPhoneAvailMem) * 1.0 / mPhoneTotalMem);
                    mMTvp_sd_mess.setMessage("sd卡可用内存" + Formatter.formatFileSize(getApplicationContext(), mSDcardAvailMem));

                    mMTvp_sd_mess.setProgress((mSDcardTotalMem - mSDcardAvailMem) * 1.0 / mSDcardTotalMem);
                    //mTv_tag.setText("用户软件(" + mUserAllInstalledAppInfos.size() + ")");

                    //设置进度条的值
                    //mPb_progress.setProgress((int)(mPhoneAvailMem / mPhoneTotalMem * 100));
                    //更新listview数据
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {

                //1.发送加载进度的消息
                mHandler.obtainMessage(LOADING).sendToTarget();
                //2.获取数据
                //所有app信息
                mAllInstalledAppInfos = AppInfoUtils.getAllInstalledAppInfos(getApplicationContext());
                mSystemAllInstalledAppInfos.clear();
                mUserAllInstalledAppInfos.clear();
                //分类
                for (AppInfoBean appInfoBean : mAllInstalledAppInfos) {
                    if (appInfoBean.isSystem()) {
                        //是系统app
                        mSystemAllInstalledAppInfos.add(appInfoBean);
                    } else {
                        //用户app
                        mUserAllInstalledAppInfos.add(appInfoBean);
                    }
                }

                //分类添加
                mAllInstalledAppInfos.clear();
                mAllInstalledAppInfos.addAll(mUserAllInstalledAppInfos);
                mAllInstalledAppInfos.addAll(mSystemAllInstalledAppInfos);

                SystemClock.sleep(2000);

                mPhoneAvailMem = AppInfoUtils.getPhoneAvailMem();
                mPhoneTotalMem = AppInfoUtils.getPhoneTotalMem();
                mSDcardAvailMem = AppInfoUtils.getSDcardAvailMem();
                mSDcardTotalMem = AppInfoUtils.getSDcardTotalMem();

                //3.发送数据加载完成
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        @Override
        public int getCount() {
            //return mSystemAllInstalledAppInfos.size()+ mUserAllInstalledAppInfos.size();
            return mAllInstalledAppInfos.size();
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
            //view的缓存

            /*//2种 1.listview 2.textview

            //1 textview 显示的位置
            if (position == 0) {
                //用户软件的标签
                TextView tv_usertag = new TextView(getApplicationContext());
                tv_usertag.setBackgroundColor(Color.GRAY);
                tv_usertag.setTextColor(Color.WHITE);
                tv_usertag.setText("用户软件(" + mUserAllInstalledAppInfos.size() + ")");
                return tv_usertag;
            } else if (position == mUserAllInstalledAppInfos.size() + 1) {
                //系统软件的标签
                TextView tv_usertag = new TextView(getApplicationContext());
                tv_usertag.setBackgroundColor(Color.GRAY);
                tv_usertag.setTextColor(Color.WHITE);
                tv_usertag.setText("系统软件(" + mSystemAllInstalledAppInfos.size() + ")");
                return tv_usertag;
            }
*/
            //用户软件或者系统软件
            ViewHolder viewHolder = null;
            if (convertView != null) {
                //有缓存view并且不是TextView
                viewHolder = (ViewHolder) convertView.getTag();
            } else {
                //没有缓存
                convertView = View.inflate(getApplicationContext(), R.layout.item_appmanager_lv, null);

                viewHolder = new ViewHolder();
                viewHolder.tv_appLocation = (TextView) convertView.findViewById(R.id.tv_appmanager_lv_applocation);
                viewHolder.tv_appName = (TextView) convertView.findViewById(R.id.tv_appmanager_lv_appname);
                viewHolder.tv_appSize = (TextView) convertView.findViewById(R.id.tv_appmanager_lv_appsize);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_appmanager_lv_icon);
                //设置标记
                convertView.setTag(viewHolder);
            }

            //取值赋值
            AppInfoBean bean = null;
            /*if (position <= mUserAllInstalledAppInfos.size()) {
                //用户数据
                bean = mUserAllInstalledAppInfos.get(position);
            } else {
                bean = mSystemAllInstalledAppInfos.get(position - mUserAllInstalledAppInfos.size());
            }*/
            bean = mAllInstalledAppInfos.get(position);

            //显示数据
            viewHolder.tv_appLocation.setText(bean.isSD() ? "sd存储" : "rom存储");
            viewHolder.tv_appName.setText(bean.getAppName());
            viewHolder.tv_appSize.setText(Formatter.formatFileSize(getApplicationContext(), bean.getSize()));
            viewHolder.iv_icon.setImageDrawable(bean.getIcon());
            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {

            AppInfoBean appInfoBean = mAllInstalledAppInfos.get(position);

            TextView tv_usertag = new TextView(getApplicationContext());
            tv_usertag.setBackgroundColor(Color.GRAY);
            tv_usertag.setTextColor(Color.WHITE);

            if (!appInfoBean.isSystem()) {
                tv_usertag.setText("用户软件(" + mUserAllInstalledAppInfos.size() + ")");
            } else {
                tv_usertag.setText("系统软件(" + mSystemAllInstalledAppInfos.size() + ")");

            }
            return tv_usertag;
        }

        @Override
        public long getHeaderId(int position) {
            AppInfoBean appInfoBean = mAllInstalledAppInfos.get(position);

            if (!appInfoBean.isSystem()) {
                return 1;
            } else {

                return 2;
            }
        }
    }

    private class ViewHolder {
        TextView tv_appName;
        TextView tv_appLocation;
        TextView tv_appSize;
        ImageView iv_icon;

    }

    private void initView() {
        setContentView(R.layout.activity_app_manager);

        mLv_datas = (StickyListHeadersListView) findViewById(R.id.lv_appmanager_viewdatas);
        mLl_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);
        mAdapter = new MyAdapter();
        mLv_datas.setAdapter(mAdapter);
        //mTv_tag = (TextView) findViewById(R.id.tv_appmanager_lv_tag);

        mTvp_rom_mess = (TextProgressView) findViewById(R.id.tpv_appmanager_rom_mess);
        mMTvp_sd_mess = (TextProgressView) findViewById(R.id.tpv_appmanager_sd_mess);
    }

}

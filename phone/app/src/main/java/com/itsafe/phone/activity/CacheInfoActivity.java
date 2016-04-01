package com.itsafe.phone.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itsafe.phone.R;
import com.itsafe.phone.domain.AppInfoBean;
import com.itsafe.phone.utils.AppInfoUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

public class CacheInfoActivity extends Activity {

    private static final int BEGINNING = 1;
    private static final int SCANNING = 2;
    private static final int FINISH = 3;
    private Object mCacheInfo;
    private ImageView mIv_scan;
    private LinearLayout mLl_scanresult;
    private ProgressBar mPb_scanprogress;
    private TextView mTv_appname;
    private RotateAnimation mRa;
    private TextView mTv_nocache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initAnimation();
        scanCache();
    }

    private void initAnimation() {
        //旋转动画
        mRa = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRa.setDuration(2000);
        mRa.setRepeatCount(-1);
        mRa.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float x) {
                return 2 * x;
            }
        });

    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BEGINNING:
                    mIv_scan.startAnimation(mRa);
                    mTv_nocache.setVisibility(View.GONE);
                    mLl_scanresult.setVisibility(View.VISIBLE);
                    break;

                case SCANNING: {//扫描中
                    ScanInfo info = (ScanInfo) msg.obj;
                    mTv_appname.setText("正在扫描: " + info.appName);
                    mPb_scanprogress.setMax(info.max);
                    mPb_scanprogress.setProgress(info.progress);

                    //扫描的结果添加到LinearLayout中
                    //扫描信息
                    //View显示
                    View view = View.inflate(getApplicationContext(), R.layout.item_antivirus_ll, null);
                    TextView tv_name = (TextView) view.findViewById(R.id.tv_item_antivirus_lv_appname);
                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_antivirus_lv_icon);
                    ImageView iv_isVirus = (ImageView) view.findViewById(R.id.iv_item_antivirus_isvirus);
                    iv_isVirus.setVisibility(View.GONE);

                    tv_name.setText(info.appName);
                    iv_icon.setImageDrawable(info.icon);

                    //添加到LinearLayout中
                    //最后扫描的最先看到 倒着添加
                    mLl_scanresult.addView(view, 0);

                    break;
                }

                case FINISH://扫描完成
                    mTv_appname.setText("扫描完成");
                    mLl_scanresult.removeAllViews();//移除所有的字view
                    //停止动画
                    mIv_scan.clearAnimation();
                    //显示结果
                    if (infos.size() > 0) {
                        //有缓存信息
                        //读取缓存信息
                        for (final CacheInfo i : infos) {
                            View v = View.inflate(getApplicationContext(), R.layout.item_cacheinfo_ll, null);
                            ImageView iv_icon = (ImageView) v.findViewById(R.id.iv_item_cacheinfo_lv_icon);
                            TextView tv_name = (TextView) v.findViewById(R.id.tv_item_cacheinfo_lv_appname);
                            TextView tv_size = (TextView) v.findViewById(R.id.tv_item_cacheinfo_cachesize);

                            iv_icon.setImageDrawable(i.icon);
                            tv_name.setText(i.name);
                            tv_size.setText(Formatter.formatFileSize(getApplicationContext(), i.size));

                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //打开设置中心的界面
                                    Intent setting = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    setting.setData(Uri.parse("package:"+ i.packName));
                                    startActivity(setting);

                                }
                            });
                            //添加ll中
                            mLl_scanresult.addView(v, 0);
                        }

                        //提醒用户清理缓存
                        Toast.makeText(CacheInfoActivity.this, "请点击又上角按钮清理缓存", Toast.LENGTH_SHORT).show();

                    } else {
                        //没有缓存
                        mLl_scanresult.setVisibility(View.GONE);
                        mTv_nocache.setVisibility(View.VISIBLE);
                        Toast.makeText(CacheInfoActivity.this, "您的手机快步如飞", Toast.LENGTH_SHORT).show();

                    }
                    break;
            }
        }
    };

    private void scanCache() {
        //耗时
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1.开始扫描的消息
                mHandler.obtainMessage(BEGINNING).sendToTarget();
                int progress = 0;
                //2.获取所有apk
                List<AppInfoBean> allInstalledAppInfos = AppInfoUtils.getAllInstalledAppInfos(getApplicationContext());
                for (AppInfoBean appInfoBean : allInstalledAppInfos) {
                    //扫描每个apk

                    getCacheInfo(appInfoBean);
                    progress++;

                    ScanInfo info = new ScanInfo();
                    info.appName = appInfoBean.getAppName();
                    info.progress = progress;
                    info.max = allInstalledAppInfos.size();
                    info.icon = appInfoBean.getIcon();
                    //发消息
                    Message msg = mHandler.obtainMessage(SCANNING);
                    msg.obj = info;
                    mHandler.sendMessage(msg);

                    SystemClock.sleep(100);
                }

                //3.发送扫描完成消息
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private class ScanInfo {
        String appName;
        int progress;
        int max;
        Drawable icon;
    }

    private void initView() {
        setContentView(R.layout.activity_cache_info);

        //扇形的图片
        mIv_scan = (ImageView) findViewById(R.id.iv_cacheinfo_scanning);

        mLl_scanresult = (LinearLayout) findViewById(R.id.ll_cacheinfo_result);
        mPb_scanprogress = (ProgressBar) findViewById(R.id.pb_cacheinfo_pb);
        mTv_appname = (TextView) findViewById(R.id.tv_cacheinfo_scanappname);

        mTv_nocache = (TextView) findViewById(R.id.tv_cacheinfo_nocache);

    }

    public void clearCache(View view) {
        //清理所有缓存
        // public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
        PackageManager mPm = getPackageManager();
        // 反射

        try {
            class ClearCacheObserver extends IPackageDataObserver.Stub {
                public void onRemoveCompleted(final String packageName, final boolean succeeded) {
                    //子线程中执行
                    //处理回调结果
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mLl_scanresult.removeAllViews();
                            mLl_scanresult.setVisibility(View.GONE);
                            mTv_nocache.setVisibility(View.VISIBLE);
                        }
                    });

                }
            }
            // 1 . class
            Class type = mPm.getClass();
            // 2. method
            Method method = type.getDeclaredMethod("freeStorageAndNotify", long.class,
                    IPackageDataObserver.class);
            //3. mPm
            //4. invoke
            method.invoke(mPm, Long.MAX_VALUE,new ClearCacheObserver());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CacheInfo {
        Drawable icon;
        String name;
        long size;
        String packName;
    }

    private List<CacheInfo> infos = new Vector<>();

    private void getCacheInfo(AppInfoBean bean) {
        // 1. mPm.getPackageSizeInfo(packageName,
        // mBackgroundHandler.mStatsObserver);
        // 2. final IPackageStatsObserver.Stub mStatsObserver = new
        // IPackageStatsObserver.Stub()
        PackageManager mPm = getPackageManager();
        // 反射

        try {
            class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
                private AppInfoBean bean;

                public MyIPackageStatsObserver(AppInfoBean bean) {
                    this.bean = bean;
                }

                @Override
                public void onGetStatsCompleted(PackageStats pStats,
                                                boolean succeeded) throws RemoteException {
                    // 回调结果 子线程
                    //System.out.println(Formatter.formatFileSize(getApplicationContext(),pStats.cacheSize));
                    //缓存大小
                    if (pStats.cacheSize > 0) {
                        //
                        CacheInfo info = new CacheInfo();

                        info.size = pStats.cacheSize;
                        info.icon = bean.getIcon();
                        info.name = bean.getAppName();
                        info.packName = bean.getPackName();
                        infos.add(info);

                    }
                }

            }
            ;
            // 1 . class
            Class type = mPm.getClass();
            // 2. method
            Method method = type.getDeclaredMethod("getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
            //3. mPm
            //4. invoke
            method.invoke(mPm, bean.getPackName(), new MyIPackageStatsObserver(bean));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

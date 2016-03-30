package com.itsafe.phone.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.itsafe.phone.R;
import com.itsafe.phone.dao.AntiVirusDao;
import com.itsafe.phone.domain.AppInfoBean;
import com.itsafe.phone.utils.AppInfoUtils;
import com.itsafe.phone.utils.Md5Utils;

import java.util.List;

/**
 * 病毒的查杀
 */
public class AntiVirusActivity extends AppCompatActivity {

    private static final int SCANING = 1;//正在扫描
    private static final int FINISH = 2;
    private static final int STARTSCAN = 3;
    private LinearLayout mLl_scan_result;
    private TextView mTv_isvirusshow;
    private Button mBt_rescan;
    private LinearLayout mLl_scanProgress;
    private CircleProgress mCp_scanProgress;
    private TextView mTv_scaninfo;
    private LinearLayout mLl_scanappinfo;
    private boolean mIsVirus;
    private LinearLayout mLl_animator_result;
    private ImageView mIv_left;
    private ImageView mIv_right;
    private AnimatorSet mAsClose;
    private AnimatorSet mAsOpen;

    private boolean isInit = false;
    private boolean interruptScaning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        //closeShowScaningProgress();



        startScan();//扫描
    }

    private void initEvent() {
        mAsOpen.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //禁用按钮
                mBt_rescan.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //启用重新扫描按钮
                mBt_rescan.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //动画结束开始扫描
        mAsClose.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束开始扫描
                startScan();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        //重新扫描
        mBt_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeShowScaningProgress();
            }
        });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STARTSCAN://开始扫描
                    mLl_scanappinfo.removeAllViews();//先移除所有view
                    //显示扫描进度
                    mLl_scanProgress.setVisibility(View.VISIBLE);
                    //隐藏扫描结果
                    mLl_scan_result.setVisibility(View.GONE);
                    //隐藏动画
                    mLl_animator_result.setVisibility(View.GONE);
                    break;
                case SCANING://正在扫描
                    //获取结果
                    ScanAppInfo info = (ScanAppInfo) msg.obj;

                    //进度的显示
                    //mCp_scanProgress.setMax(info.max);
                    mCp_scanProgress.setProgress((int)((info.currentProgress * 100.0) / info.max ));


                    //扫描信息
                    //View显示
                    View view = View.inflate(getApplicationContext(), R.layout.item_antivirus_ll, null);
                    TextView tv_name = (TextView) view.findViewById(R.id.tv_item_antivirus_lv_appname);
                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item_antivirus_lv_icon);
                    ImageView iv_isVirus = (ImageView) view.findViewById(R.id.iv_item_antivirus_isvirus);

                    tv_name.setText(info.appName);
                    iv_icon.setImageDrawable(info.icon);
                    if (info.isVirus) {
                        mIsVirus = info.isVirus;
                        iv_isVirus.setImageResource(R.mipmap.check_status_red);
                    } else {
                        iv_isVirus.setImageResource(R.mipmap.check_status_green);
                    }

                    //添加到LinearLayout中
                    //最后扫描的最先看到 倒着添加
                    mLl_scanappinfo.addView(view, 0);
                    break;

                case FINISH://扫描完成
                    //拍照
                    mLl_scanProgress.setDrawingCacheEnabled(true);
                    mLl_scanProgress.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    //获取进度完成的照片
                    Bitmap progressImage = mLl_scanProgress.getDrawingCache();//前提setDrawingCacheEnabled(true)
                    //左半照片
                    Bitmap leftBitmap = getLeftImage(progressImage);
                    //右半照片
                    Bitmap rightBitmap = getRightImage(progressImage);

                    //设置图片
                    mIv_left.setImageBitmap(leftBitmap);
                    mIv_right.setImageBitmap(rightBitmap);

                    if (!isInit) {
                        initOpenShowResult();
                        initCloseShowScaningProgress();
                        initEvent();
                        isInit = true;
                    }

                    //隐藏扫描进度
                    mLl_scanProgress.setVisibility(View.GONE);
                    mLl_scan_result.setVisibility(View.VISIBLE);

                    //显示动画的背景
                    mLl_animator_result.setVisibility(View.VISIBLE);
                    if (mIsVirus) {
                        mTv_isvirusshow.setText("手机又病毒,看下面记录");
                    } else {
                        mTv_isvirusshow.setText("手机无病毒,很安全");
                    }

                    openShowResult();//打开拍照界面,显示结果的动画
                    break;

            }
        }
    };

    private void openShowResult() {
        //照片打开的动画
        mAsOpen.start();


    }

    private void initOpenShowResult() {
        //属性动画集
        mAsOpen = new AnimatorSet();
        mLl_animator_result.measure(0,0);//布局参数随意测量
        //添加属性动画
        int width = mLl_animator_result.getMeasuredWidth() / 2;
        //1. 添加左图移开的动画0 到 -width
        //mIv_left.setTranslationX(translationX);
        ObjectAnimator leftTransationOut = ObjectAnimator.ofFloat(mIv_left,"translationX",0,-width);

        //2.添加左图移开的alpha动画
        ObjectAnimator leftAlphaOut = ObjectAnimator.ofFloat(mIv_left, "alpha", 1.0f, 0);

        //3. 添加右图移开的动画0 到 width
        ObjectAnimator rightTransationOut = ObjectAnimator.ofFloat(mIv_right,"translationX",0,width);

        //4. 添加右图移开的alpha动画
        ObjectAnimator rightAlphaOut = ObjectAnimator.ofFloat(mIv_right, "alpha", 1.0f, 0);

        //5. 显示结果的view alpha渐变显示
        ObjectAnimator resultAlphaShow = ObjectAnimator.ofFloat(mLl_scan_result, "alpha", 0, 1.0f);

        mAsOpen.playTogether(leftTransationOut, leftAlphaOut, rightTransationOut, rightAlphaOut, resultAlphaShow);
        mAsOpen.setDuration(1000);
    }

    /**
     * 关闭拍照背景,重新开始扫描
     */
    private void closeShowScaningProgress() {
        //照片打开的动画
        mAsClose.start();




    }

    private void initCloseShowScaningProgress() {
        //属性动画集
        mAsClose = new AnimatorSet();
        mLl_animator_result.measure(0,0);//布局参数随意测量
        //添加属性动画
        int width = mLl_animator_result.getMeasuredWidth() / 2;
        //1. 添加左图移开的动画0 到 -width
        //mIv_left.setTranslationX(translationX);
        ObjectAnimator leftTransationOut = ObjectAnimator.ofFloat(mIv_left, "translationX", -width, 0);

        //2.添加左图移开的alpha动画
        ObjectAnimator leftAlphaOut = ObjectAnimator.ofFloat(mIv_left, "alpha", 0, 1.0f);

        //3. 添加右图移开的动画0 到 width
        ObjectAnimator rightTransationOut = ObjectAnimator.ofFloat(mIv_right, "translationX", width, 0);

        //4. 添加右图移开的alpha动画
        ObjectAnimator rightAlphaOut = ObjectAnimator.ofFloat(mIv_right, "alpha", 0, 1.0f);

        //5. 显示结果的view alpha渐变显示
        ObjectAnimator resultAlphaShow = ObjectAnimator.ofFloat(mLl_scan_result, "alpha", 1.0f, 0);

        mAsClose.playTogether(leftTransationOut, leftAlphaOut, rightTransationOut, rightAlphaOut, resultAlphaShow);
        mAsClose.setDuration(1000);
    }


    private Bitmap getLeftImage(Bitmap progressImage) {
        int width = progressImage.getWidth() / 2;
        int height = progressImage.getHeight();
        //空的画纸
        Bitmap leftImage = Bitmap.createBitmap(width, height, progressImage.getConfig());

        //画板
        Canvas canvas = new Canvas(leftImage);//把画纸放到画板中
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        canvas.drawBitmap(progressImage,matrix,paint);
        return leftImage;
    }

    private Bitmap getRightImage(Bitmap progressImage) {
        int width = progressImage.getWidth() / 2;
        int height = progressImage.getHeight();
        //空的画纸
        Bitmap rightImage = Bitmap.createBitmap(width, height, progressImage.getConfig());

        //画板
        Canvas canvas = new Canvas(rightImage);//把画纸放到画板中
        Matrix matrix = new Matrix();
        matrix.setTranslate(-width,0);//画原图的右半部分
        Paint paint = new Paint();
        canvas.drawBitmap(progressImage,matrix,paint);
        return rightImage;
    }

    private void startScan() {
        //耗时
        new Thread() {
            @Override
            public void run() {
                super.run();
                //1. 发送开始扫描的消息
                mHandler.obtainMessage(STARTSCAN).sendToTarget();

                //2. 开始扫描每个安装的apk
                //获取所有安装的apk
                List<AppInfoBean> allInstalledAppInfos = AppInfoUtils.getAllInstalledAppInfos(getApplicationContext());
                int progress = 0;
                for (AppInfoBean appInfoBean : allInstalledAppInfos) {
                    if (interruptScaning) {
                        return;
                    }
                    progress++;
                    String sourceDir = appInfoBean.getSourceDir();//apk的安装目录
                    //计算MD5
                    String md5 = Md5Utils.getFileMd5(sourceDir);
                    //判断
                    boolean isVirus = AntiVirusDao.isVirus(md5);

                    //封装扫描结果
                    //图标 名字 是否是病毒
                    ScanAppInfo info = new ScanAppInfo();
                    info.icon = appInfoBean.getIcon();
                    info.appName = appInfoBean.getAppName();
                    info.isVirus = isVirus;
                    info.max = allInstalledAppInfos.size();
                    info.currentProgress = progress;

                    //发送扫描结果
                    Message msg = mHandler.obtainMessage(SCANING);
                    msg.obj = info;
                    mHandler.sendMessage(msg);

                    SystemClock.sleep(100);

                }

                //扫描结束
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private class ScanAppInfo {
        Drawable icon;
        String appName;
        boolean isVirus;
        int max;
        int currentProgress;


    }

    private void initView() {
        setContentView(R.layout.activity_anti_virus);

        //扫描结果的根布局
        mLl_scan_result = (LinearLayout) findViewById(R.id.ll_antivirus_showresult);

        mTv_isvirusshow = (TextView) findViewById(R.id.tv_antivirus_scanresult);
        mBt_rescan = (Button) findViewById(R.id.bt_antivirus_rescan);

        //扫描进度
        mLl_scanProgress = (LinearLayout) findViewById(R.id.ll_antivirus_scanprogerss);

        mCp_scanProgress = (CircleProgress) findViewById(R.id.cp_antivirus_progress);
        mTv_scaninfo = (TextView) findViewById(R.id.tv_antivirus_scaninfos);

        //扫描app信息
        mLl_scanappinfo = (LinearLayout) findViewById(R.id.ll_antivirus_scanappinfo);

        mLl_animator_result = (LinearLayout) findViewById(R.id.ll_antivirus_animator_result);

        mIv_left = (ImageView) findViewById(R.id.iv_antivirus_leftimage);
        mIv_right = (ImageView) findViewById(R.id.iv_antivirus_rightimage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时关闭扫描病毒的线程
        interruptScaning = true;
    }
}

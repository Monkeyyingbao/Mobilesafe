package com.itsafe.phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends Activity {

    private RelativeLayout mRl_splash;
    private TextView mTv_version;
    private AnimationSet mAs;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //            String description = (String) msg.obj;
            //            Toast.makeText(SplashActivity.this, description, Toast.LENGTH_SHORT).show();
            switch (msg.what) {
                //提示当前是最新版
                case StrUtils.BEST_VERSION:
                    String str = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    break;
                //新版本提示
                case StrUtils.IS_UPDATE:
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("下载新版本"+mVersionName);
                    builder.setMessage(VersionInfo.description);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //下载apk
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
            }
        }
    };
    private int mVersionCode;//当前版本号
    private String mVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //界面
        initView();
        //数据
        initData();
        //事件
        initEvent();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        mRl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        mTv_version = (TextView) findViewById(R.id.tv_version);
        initAnimation();
    }

    private void initData() {
        //显示版本名称
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(this.getPackageName(), 0);
            mVersionCode = info.versionCode;
            mVersionName = info.versionName;
            mTv_version.setText(mVersionName+mVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        //监听动画播放
        mAs.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                System.out.println("动画开始");
                //动画开始:初始化数据(子线程),初始化网络(子线程),版本更新
                //如果用户勾选了应用更新提示,要执行版本检测
                if (SPUtils.getBoolean(SplashActivity.this, StrUtils.AUTO_CHECK_VERSION, true)) {
                    checkVersion();
                } else {
                    //等动画播放
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束时进入主界面
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void checkVersion() {
        //检测版本//安装//界面跳转
        new Thread(){
            @Override
            public void run() {
                System.out.println("开始请求网络");
                //请求网络数据,定义方法
                readUrlData();
        //判断是否需要更新,并提示
                System.out.println("判断是否需要更新,并提示");
                if (mVersionCode == VersionInfo.versionCode) {
                    //提示提示最新版
                    Message msg = Message.obtain();
                    msg.what = StrUtils.BEST_VERSION;
                    msg.obj = "已是最新版";
                    mHandler.sendMessage(msg);
                } else {
                    //提示有新版本,是否更新+新版本描述
                    Message msg = Message.obtain();
                    msg.what = StrUtils.IS_UPDATE;
                    mHandler.sendMessage(msg);
                    Log.i("update", "run: "+"告诉handler有新版本");
                }
            }
        }.start();
    }

    private void readUrlData() {
        //访问URL,了解数据格式,做json解析,http://192.168.1.7:8080/version.json
        //{"versioncode","1.0","versionname","土豆版","description","添加了NB功能","download","http://192.168.1.7:8080/mobile.apk"}
        try {
            URL url = new URL(getResources().getString(R.string.version_url));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            System.out.println("结果码"+code);
            if (code == 200) {
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "gb2312"));
                StringBuilder sb = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    line = reader.readLine();
                }
                reader.close();
                String json = sb.toString();
                //解析json数据
                JSONObject jsonObject = new JSONObject(json);
                VersionInfo.versionCode = jsonObject.getInt("versioncode");
                VersionInfo.versionName = jsonObject.getString("versionname");
                VersionInfo.description = jsonObject.getString("description");
                VersionInfo.download = jsonObject.getString("download");
                Log.i("json", "readUrlData: " + VersionInfo.versionCode + VersionInfo.versionName + VersionInfo.description + VersionInfo.download);
            } else {
                //请求网络失败,无法检测更新
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static class VersionInfo {
        private static int versionCode;
        private static String versionName;
        private static String description;
        private static String download;

    }

    private void initAnimation() {
        //补间动画
        //旋转动画
        RotateAnimation ra = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //比例动画
        ScaleAnimation sa = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //渐变动画
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        //动画集
        mAs = new AnimationSet(true);
        mAs.setDuration(2000);
        mAs.setFillAfter(true);
        mAs.addAnimation(ra);
        mAs.addAnimation(sa);
        mAs.addAnimation(aa);
        mRl_splash.startAnimation(mAs);
    }
}

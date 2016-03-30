package com.itsafe.phone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itsafe.phone.R;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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

            switch (msg.what) {
                //提示当前是最新版
                case StrUtils.BEST_VERSION:
                    Toast.makeText(SplashActivity.this, "已是最新版", Toast.LENGTH_SHORT).show();
                    startHome();
                    break;
                //新版本提示
                case StrUtils.IS_UPDATE:
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setCancelable(false);//对话框不可撤销
                    builder.setTitle("下载新版本" + VersionInfo.versionName+VersionInfo.versionCode);
                    builder.setMessage(VersionInfo.description);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //下载apk
                            downloadAPK();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startHome();
                        }
                    });
                    builder.show();
                    break;
                case StrUtils.FAIL_CONN:
                    Toast.makeText(SplashActivity.this, "请求网络失败,无法检测更新", Toast.LENGTH_SHORT).show();
                    startHome();
                    break;
                default:
                    //默认处理异常
                    switch (msg.what) {
                        case 1001:
                            Toast.makeText(SplashActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                            break;
                        case 1002:
                            Toast.makeText(SplashActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                            break;
                        case 1003:
                            Toast.makeText(SplashActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(SplashActivity.this, "服务器连接超时", Toast.LENGTH_SHORT).show();
                    }
                    startHome();
            }
        }
    };

    private void downloadAPK() {
        //xUtils下载
        HttpUtils httpUtils = new HttpUtils();
        final File sdDir = Environment.getExternalStorageDirectory();
        //先判断文件夹中是否存在,如果存在要先删除
        File oldApk = new File(sdDir,"mobilesafe.apk");
        if (oldApk.exists()) {
            oldApk.delete();
        }
        httpUtils.download(VersionInfo.download, sdDir.getAbsolutePath()+"/mobilesafe.apk",true,true, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载成功提示安装
                Toast.makeText(SplashActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
               /*系统安装器的意图过滤器
				<intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="content" />
                <data android:scheme="file" />
                <data android:mimeType="application/vnd.android.package-archive" />
                 </intent-filter>
				 */
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                File file = new File(Environment.getExternalStorageDirectory(),"mobilesafe.apk");
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivityForResult(intent,1);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //下载失败
                Log.i("onFailure", "onFailure: "+s);
                startHome();
            }
        });
    }
    //监控打开的Activity结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回主界面
        startHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

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

    /**
     * 拷贝到//data/data/com.itsafe.phone
     * @param dbFileName 数据库的文件名(asset目录)
     */
    private void copyDB(String dbFileName) throws IOException {
        //判断文件是已拷贝过否
        File filesDir = getFilesDir();///data/data/com.itsafe.phone/files
        File file = new File(filesDir, dbFileName);
        if (file.exists()) {
            return;
        }

        //流的拷贝
        //输入流
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open(dbFileName);
        //输出流
        FileOutputStream fos = new FileOutputStream(file);
        //读数据
        byte[] buffer = new byte[1024 * 5];
        int len = inputStream.read(buffer);
        while (len != -1) {
            //写数据
            fos.write(buffer,0,len);
            fos.flush();//刷新缓冲区的内容到目的地
            //继续读取
            len = inputStream.read(buffer);
        }
        //关闭流
        inputStream.close();
        fos.close();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_splash);
        mRl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        mTv_version = (TextView) findViewById(R.id.tv_version);
        initAnimation();
    }
    /**
     * 初始化数据
     */
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

    /**
     * 初始化事件
     */
    private void initEvent() {
        //监听动画播放
        mAs.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                System.out.println("动画开始");
                //号码归属地文件的拷贝
                copyFileThread("address.db");
                //服务号码文件拷贝
                copyFileThread("commonnum.db");
                //病毒数据库的拷贝
                copyFileThread("antivirus.db");
                //动画开始:初始化数据(子线程),初始化网络(子线程),版本更新
                //如果用户勾选了应用更新提示,要执行版本检测
                if (SPUtils.getBoolean(SplashActivity.this, StrUtils.AUTO_CHECK_VERSION, false)) {
                    checkVersion();
                } else {
                    //等动画播放
                }
            }

            //耗时线程拷贝文件
            private void copyFileThread(final String fileName) {
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            copyDB(fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束时进入主界面
                if (SPUtils.getBoolean(SplashActivity.this, StrUtils.AUTO_CHECK_VERSION, false)) {
                } else {
                    startHome();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        Log.i("home", "startHome:进入了主界面 ");
        finish();
    }

    private void checkVersion() {
        //检测版本//安装//界面跳转
        new Thread(){
            @Override
            public void run() {
                System.out.println("开始请求网络");
                //请求网络数据,定义方法
                readUrlData();

            }
        }.start();
    }

    /**
     * 联网检测结束后,要计算时间,如果过快要延时,等带动画播完
     */
    private void readUrlData() {
        Message msg = Message.obtain();
        long startTime = System.currentTimeMillis();
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

                //判断是否需要更新,并提示
                System.out.println("判断是否需要更新,并提示");
                if (mVersionCode == VersionInfo.versionCode) {
                    //提示最新版
                    msg.what = StrUtils.BEST_VERSION;
                } else {
                    //提示有新版本,是否更新+新版本描述
                    msg.what = StrUtils.IS_UPDATE;
                    Log.i("update", "run: " + "告诉handler有新版本");
                }
            } else {
                //请求网络失败,无法检测更新
                Log.i("logi", "readUrlData: 请求网络失败,无法检测更新");
                msg.what = StrUtils.FAIL_CONN;
            }
        } catch (MalformedURLException e) {
            msg.what = 1001;
            e.printStackTrace();
        } catch (IOException e) {
            msg.what = 1002;
            e.printStackTrace();
        } catch (JSONException e) {
            msg.what = 1003;
            e.printStackTrace();
        }finally {
            long endTime = System.currentTimeMillis();
            if ((endTime-startTime) < 2000) {
                SystemClock.sleep(2000 - (endTime - startTime));
            }
        mHandler.sendMessage(msg);
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

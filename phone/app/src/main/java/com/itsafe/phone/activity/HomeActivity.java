package com.itsafe.phone.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itsafe.phone.R;
import com.itsafe.phone.utils.Md5Utils;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.StrUtils;

public class HomeActivity extends Activity {

    private ImageView mIv_logo;//logo旋转图标
    private ImageView mIv_setting;//设置按钮
    private GridView mGv_tools;

    public static final String[] names = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "病毒查杀", "缓存清理", "高级工具"};
    public static final String[] desc = {"手机丢失好找", "防骚扰防监听", "方便管理软件", "保持手机通畅", "注意流量超标", "手机安全保障", "手机快步如飞", "特性处理更好"};
    public static final int[] icons = {R.drawable.sjfd, R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
            R.drawable.sjsd, R.drawable.hcql, R.drawable.szzx};
    private AlertDialog mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //初始化界面
        initView();
        //开始动画
        startAnimation();
        //设置数据
        initData();
        //初始化事件
        initEvent();
    }

    private void initEvent() {

        //添加设置中心的按钮事件
        mIv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到设置中心的界面
                Intent intent = new Intent(HomeActivity.this, SettingCenterActivity.class);
                startActivity(intent);
            }
        });
        //设置gridview条目的点击监听事件
        mGv_tools.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://点击了手机防盗
                        //如果首次登陆显示设置密码对话框
                        String password = SPUtils.getString(HomeActivity.this, StrUtils.PASSWORD, null);
                        if (TextUtils.isEmpty(password)) {
                            //没设置过密码
                            showSetPasswordDailog();
                        } else {
                            //设置过密码,显示输入密码对话框
                            showEnterPasswordDialog();
                        }
                        break;
                    case 1://黑名单管理
                        Intent black = new Intent(HomeActivity.this, AndroidBlackActivity.class);
                        startActivity(black);
                        break;
                    case 7://高级工具
                        Intent atool = new Intent(HomeActivity.this,AToolActivity.class);
                        startActivity(atool);
                        break;



                }
            }
        });
    }

    /**
     * 输入密码的对话框
     */
    private void showEnterPasswordDialog() {
        //自定义view对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.dialog_password, null);
        //输入密码的文本框
        final EditText et_pass1 = (EditText) view.findViewById(R.id.et_dialog_password1);
        final EditText et_pass2 = (EditText) view.findViewById(R.id.et_dialog_password2);
        et_pass2.setVisibility(View.GONE);//不占用空间
        //确定按钮
        final Button bt_confirm = (Button) view.findViewById(R.id.bt_dialog_confirm);
        //取消按钮
        Button bt_cancel = (Button) view.findViewById(R.id.bt_dialog_cancel);
        //两个按钮同意的监听事件
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //点击确认
                if (v == bt_confirm) {
                    //先获取到密码
                    String pass1 = et_pass1.getText().toString().trim();
                    //判断密码是否符合规则
                    //判断是否为空
                    if (TextUtils.isEmpty(pass1)) {
                        //提醒不能为空
                        Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                        //判断密码的MD5值是否一致
                    } else if (Md5Utils.encode(pass1).equals(SPUtils.getString(getApplicationContext(), StrUtils.PASSWORD, null))) {
                        //密码正确,进入手机防盗界面
                        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                        startActivity(intent);
                        Log.i("log", "onClick:密码正确,进入手机防盗界面 ");
                    } else {
                        //密码错误
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //如果密码设置完毕关闭对话框
                    mBuilder.dismiss();
                } else {
                    mBuilder.dismiss();
                }
            }
        };
        bt_cancel.setOnClickListener(listener);
        bt_confirm.setOnClickListener(listener);
        builder.setView(view);
        mBuilder = builder.create();
        mBuilder.show();
    }

    /**
     * 设置密码的对话框
     */
    private void showSetPasswordDailog() {
        //自定义view对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.dialog_password, null);
        //两个输入密码的文本框
        final EditText et_pass1 = (EditText) view.findViewById(R.id.et_dialog_password1);
        final EditText et_pass2 = (EditText) view.findViewById(R.id.et_dialog_password2);
        //确定按钮
        final Button bt_confirm = (Button) view.findViewById(R.id.bt_dialog_confirm);
        //取消按钮
        Button bt_cancel = (Button) view.findViewById(R.id.bt_dialog_cancel);
        //两个按钮同意的监听事件
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //点击确认
                if (v == bt_confirm) {
                    //先获取到两个密码
                    String pass1 = et_pass1.getText().toString().trim();
                    String pass2 = et_pass2.getText().toString().trim();
                    //判断密码是否符合规则
                    //判断是否为空
                    if (TextUtils.isEmpty(pass1) || TextUtils.isEmpty(pass2)) {
                        //提醒不能为空
                        Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                        //判断两次是否一致
                    } else if (pass1.equals(pass2)) {
                        //进行保存
                        //先对密码进行MD5的加密
                        SPUtils.putString(getApplicationContext(), StrUtils.PASSWORD, Md5Utils.encode(pass1));
                        Toast.makeText(HomeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    } else {
                        //两次密码不一致
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }
                        //如果密码设置完毕关闭对话框
                        mBuilder.dismiss();
                } else {
                    mBuilder.dismiss();
                }
            }
        };
        bt_cancel.setOnClickListener(listener);
        bt_confirm.setOnClickListener(listener);
        builder.setView(view);
        mBuilder = builder.create();
        mBuilder.show();
    }

    private void initData() {
        mGv_tools.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
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
            View view = View.inflate(getApplicationContext(), R.layout.item_home_gv, null);
            TextView tv_name_gv = (TextView) view.findViewById(R.id.name_home_item);
            TextView tv_desc_gv = (TextView) view.findViewById(R.id.desc_home_item);
            ImageView iv_icon_gv = (ImageView) view.findViewById(R.id.gv_icon);
            //设置标题
            tv_name_gv.setText(names[position]);
            //设置描述
            tv_desc_gv.setText(desc[position]);
            //设置图片
            iv_icon_gv.setImageResource(icons[position]);
            return view;
        }
    }

    /**
     * 开始动画
     */
    private void startAnimation() {
        //给logo设置属性动画
        ObjectAnimator oa = ObjectAnimator.ofFloat(mIv_logo, "rotationY", 0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360);
        oa.setDuration(2000);//完成时间
        oa.setRepeatCount(ValueAnimator.INFINITE);//动画重复次数-1无限
        oa.start();
    }

    private void initView() {
        //        找到关心控件
        //旋转图标
        mIv_logo = (ImageView) findViewById(R.id.iv_home_logo);
        //设置按钮
        mIv_setting = (ImageView) findViewById(R.id.iv_home_setting);
        mGv_tools = (GridView) findViewById(R.id.gv_home_tools);
    }
}

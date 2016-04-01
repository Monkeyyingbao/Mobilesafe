package com.itsafe.phone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.itsafe.phone.R;
import com.itsafe.phone.dao.BlackDao;
import com.itsafe.phone.db.BlackDB;
import com.itsafe.phone.domain.BlackBean;
import com.itsafe.phone.utils.ShowToast;
import com.itsafe.phone.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 黑名单管理的界面
 */
public class BlackActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ImageView mIv_add;
    private LinearLayout mLl_loading;
    private ListView mLv_showDatas;
    private ImageView mIv_noData;

    //存放黑名单数据的容器
    private List<BlackBean> mBlackBeans = new ArrayList<>();
    private MyAdapter mAdapter;
    private BlackDao mBlackDao;
    private View mContentView;
    private PopupWindow mPW;
    private Animation mPopupAnimation;
    private AlertDialog mAlertDialog;
    private boolean mIsFirstShow;
    private EditText mEt_blackphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//界面
        initData();//数据 可执行多次
        initEvent();//事件
        initPopupWindow();//初始化弹出窗体
        initAddBlackDialog();//添加黑名单的对话框
    }

    private void initAddBlackDialog() {
        //ShowToast.show("shoudongAdd",this);
        //通过对话框添加数据
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //自定义view
        final View mDialogView = View.inflate(getApplicationContext(), R.layout.dialog_add_black, null);
        //获取子组件
        mEt_blackphone = (EditText) mDialogView.findViewById(R.id.et_dialog_addblack_phone);
        //拦截模式的勾选
        final CheckBox cb_sms = (CheckBox) mDialogView.findViewById(R.id.cb_dialog_addblack_smsmode);
        final CheckBox cb_phone = (CheckBox) mDialogView.findViewById(R.id.cb_dialog_addblack_phonemode);
        //添加
        Button bt_add = (Button) mDialogView.findViewById(R.id.bt_dialog_addblack_add);
        //取消
        Button bt_cancle = (Button) mDialogView.findViewById(R.id.bt_dialog_addblack_cancel);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加黑名单
                //1.判断号码不能为空
                String phone = mEt_blackphone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ShowToast.show("号码不能为空", BlackActivity.this);
                    return;
                }
                //2.拦截模式至少勾选一个
                if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
                    ShowToast.show("拦截模式至少勾选一个",BlackActivity.this);
                    return;
                }
                //3.添加黑名单数据
                BlackBean bean = new BlackBean();
                bean.setPhong(phone);
                int mode = 0;
                if (cb_phone.isChecked()) {
                    mode |= BlackDB.PHONE_MODE;
                }
                if (cb_sms.isChecked()) {
                    mode |= BlackDB.SMS_MODE;
                }
                bean.setMode(mode);
                mBlackDao.update(bean);//添加到数据库中
                mIsFirstShow = true;//是否滚动置顶标记
                //4.显示最新添加的黑名单数据
                initData();
                //5.关闭对话框
                mAlertDialog.dismiss();
            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭对话框
                mAlertDialog.dismiss();
            }
        });
        //创建对话框
        builder.setView(mDialogView);
        mAlertDialog = builder.create();
    }

    private void initPopupWindow() {
        //初始化弹出窗体
        mContentView = View.inflate(getApplicationContext(), R.layout.popupwindow_addblackdata, null);

        //获取子组件添加事件
        TextView tv_shoudong = (TextView) mContentView.findViewById(R.id.tv_popupwindow_addblack_shoudong);
        TextView tv_tel = (TextView) mContentView.findViewById(R.id.tv_popupwindow_addblack_tellog);
        TextView tv_sms = (TextView) mContentView.findViewById(R.id.tv_popupwindow_addblack_smslog);
        TextView tv_friends = (TextView) mContentView.findViewById(R.id.tv_popupwindow_addblack_friendlog);

        View.OnClickListener mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听四个操作
                switch (v.getId()) {
                    case R.id.tv_popupwindow_addblack_shoudong://手动添加
                        shoudongAdd();
                        break;
                    case R.id.tv_popupwindow_addblack_tellog://通话添加
                        tellogAdd();
                        break;
                    case R.id.tv_popupwindow_addblack_smslog://短信添加
                        smslogAdd();
                        break;
                    case R.id.tv_popupwindow_addblack_friendlog://好友添加
                        friendsAdd();
                        break;
                }
                //关闭对话框
                mPW.dismiss();
            }
        };
        tv_shoudong.setOnClickListener(mListener);
        tv_tel.setOnClickListener(mListener);
        tv_sms.setOnClickListener(mListener);
        tv_friends.setOnClickListener(mListener);

        mPW = new PopupWindow(mContentView, WindowManager.LayoutParams.WRAP_CONTENT, -2);
        //设置参数
        mPW.setFocusable(true);
        //设置背景 作用1.外部点击事件生效  2.播放动画
        mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置外部点击
        mPW.setOutsideTouchable(true);

        //初始化动画
        //播放动画
        mPopupAnimation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
        mPopupAnimation.setDuration(400);
    }

    private void friendsAdd() {
        //ShowToast.show("friendsAdd",this);
        Intent intent = new Intent(this, FriendsActivity.class);
        //获取请求结果
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //判断如果有数据就进行回显
        if (data != null) {
            //获取选择的好友
            String safeNum = data.getStringExtra(StrUtils.SAFENUMBER);
            //显示在对话框中,自动显示号码
            showDialog(safeNum);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void smslogAdd() {
        //ShowToast.show("smslogAdd", this);
        Intent intent = new Intent(this, SmsActivity.class);
        //获取请求结果
        startActivityForResult(intent,0);
    }

    private void tellogAdd() {
        //ShowToast.show("tellogAdd",this);
        Intent intent = new Intent(this, TelActivity.class);
        //获取请求结果
        startActivityForResult(intent,0);
    }

    private void shoudongAdd() {
       //显示对话框
        showDialog("");
    }

    private void showDialog(String phone) {
        //显示黑名单号码
        mEt_blackphone.setText(phone);
        mAlertDialog.show();
    }

    private void initEvent() {
        //添加新的黑名单数据
        mIv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加新的按钮被点击
                //判断窗体是否已显示
                if (mPW != null && mPW.isShowing()) {
                    //再次点击就会关闭
                    mPW.dismiss();
                } else {
                    //显示PopupWindow
                    mPW.showAsDropDown(v);
                    //显示动画
                    mContentView.startAnimation(mPopupAnimation);
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING://加载数据
                    //加载数据的进度条显示
                    mLl_loading.setVisibility(View.VISIBLE);
                    //同时隐藏ListView 和 nodata
                    mLv_showDatas.setVisibility(View.GONE);
                    mIv_noData.setVisibility(View.GONE);
                    break;
                case FINISH://加载数据完成
                    //隐藏加载数据的进度条
                    mLl_loading.setVisibility(View.GONE);
                    //有数据就显示listview 隐藏nodata
                    if (mBlackBeans.isEmpty()) {
                        mIv_noData.setVisibility(View.VISIBLE);
                        mLv_showDatas.setVisibility(View.GONE);
                    } else {
                        //没有数据显示nodata隐藏listview
                        mLv_showDatas.setVisibility(View.VISIBLE);
                        mIv_noData.setVisibility(View.GONE);
                        //刷新界面
                        mAdapter.notifyDataSetChanged();
                        if (mIsFirstShow) {
                            mLv_showDatas.smoothScrollToPosition(0);
                            mIsFirstShow = false;
                        }
                    }
                    break;
            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackBeans.size();
        }

        @Override
        public BlackBean getItem(int position) {
            return mBlackBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //界面缓存
            ViewHolder viewHolder = null;
            //判断是否有缓存
            if (convertView == null) {
                //没有缓存
                convertView = View.inflate(getApplicationContext(), R.layout.item_black_lv, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_item_black_lv_blackmode);
                viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_item_black_lv_blackphone);
                viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_item_black_lv_delete);
                //设置标签
                convertView.setTag(viewHolder);
            } else {
                //有缓存
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //取值
            final BlackBean bean = getItem(position);
            //显示值
            viewHolder.tv_phone.setText(bean.getPhong());
            switch (bean.getMode()) {
                case BlackDB.SMS_MODE://短信拦截
                    viewHolder.tv_mode.setText("短信拦截");
                    break;
                case BlackDB.PHONE_MODE://电话拦截
                    viewHolder.tv_mode.setText("电话拦截");
                    break;
                case BlackDB.ALL_MODE://全部拦截
                    viewHolder.tv_mode.setText("全部拦截");
                    break;
            }
            //删除
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除数据
                    //本地删除
                    mBlackBeans.remove(position);
                    //数据库删除
                    mBlackDao.delete(bean.getPhong());
                    //更新界面
                    mAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

    private void initData() {
        //数据过大子线程访问数据
        new Thread() {
            @Override
            public void run() {
                //获取数据
                //1.发送正在加载数据的消息
                mHandler.obtainMessage(LOADING).sendToTarget();
                //2.加载数据
                mBlackBeans = mBlackDao.findAll();
                //模拟耗时
                SystemClock.sleep(1000);
                //3.加载完成
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    /**
     * 黑名单界面
     */
    private void initView() {
        setContentView(R.layout.activity_black);
        //获取子组件view
        //添加黑名单
        mIv_add = (ImageView) findViewById(R.id.iv_black_add);
        //加载数据的根布局
        mLl_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);
        mLv_showDatas = (ListView) findViewById(R.id.lv_black_showdata);
        //没有数据
        mIv_noData = (ImageView) findViewById(R.id.iv_black_nodata);
        mAdapter = new MyAdapter();
        mLv_showDatas.setAdapter(mAdapter);//绑定适配器

        mBlackDao = new BlackDao(this);

    }
}

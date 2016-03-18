package com.itsafe.phone.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.itsafe.phone.R;
import com.itsafe.phone.utils.SPUtils;
import com.itsafe.phone.utils.ShowToast;
import com.itsafe.phone.utils.StrUtils;

public class Setup3Activity extends BaseSetupActivity {

    private EditText mEt_safenumber;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setup3);
        //安全号码的编辑框
        mEt_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
    }

    /**
     * 设置安全号码点击事件
     * @param view
     */
    public void selectSafeNum(View view) {
        //启动新的界面来显示所有的好友信息
        Intent intent = new Intent(this, FriendsActivity.class);
        //点击某个好友关闭界面,在编辑框中显示好友号码
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //判断如果有数据就进行回显
        if (data != null) {
            //获取选择的好友
            String safeNum = data.getStringExtra(StrUtils.SAFENUMBER);
            //显示在编辑框中
            mEt_safenumber.setText(safeNum);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initData() {
        //回显号码
        mEt_safenumber.setText(SPUtils.getString(this, StrUtils.SAFENUMBER, ""));
        //设置光标停留的位置
        mEt_safenumber.setSelection(mEt_safenumber.getText().toString().trim().length());
    }

    @Override
    protected void startNext() {
        //添加获取保存安全号码
        String safeNumber = mEt_safenumber.getText().toString().trim();
        //判断是否为空
        if (TextUtils.isEmpty(safeNumber)) {
            //空
            ShowToast.show("安全号码不能为空", this);
        } else {
            //有安全号码
            //保存安全号码到SP中
            SPUtils.putString(getApplicationContext(),StrUtils.SAFENUMBER,safeNumber);
            //跳转
            startPage(Setup4Activity.class);
        }
    }

    @Override
    protected void startPrev() {
        startPage(Setup2Activity.class);
    }
}

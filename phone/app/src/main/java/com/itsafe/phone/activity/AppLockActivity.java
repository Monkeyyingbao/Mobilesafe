package com.itsafe.phone.activity;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.itsafe.phone.R;
import com.itsafe.phone.dao.LockedDao;
import com.itsafe.phone.db.LockedDB;
import com.itsafe.phone.domain.AppInfoBean;
import com.itsafe.phone.utils.AppInfoUtils;
import com.itsafe.phone.view.AppLockHeadView;
import com.itsafe.phone.view.BaseLockFragment;
import com.itsafe.phone.view.LockedFragment;
import com.itsafe.phone.view.UnLockedFragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 程序锁的界面
 */
public class AppLockActivity extends FragmentActivity {

    private AppLockHeadView mAlhv_op;
    private FrameLayout mFl_content;
    private BaseLockFragment mLockedFragment;
    private BaseLockFragment mUnlockedFragment;
    private LockedDao mLockedDao;
    private List<String> mAllLockedAppPackName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initEvent();

        intiFragment();
        initData();
    }



    private void intiFragment() {
        List<AppInfoBean> allInstalledAppInfos = AppInfoUtils.getAllInstalledAppInfos(this);

        //对allInstalledAppInfos排序
        Collections.sort(allInstalledAppInfos, new Comparator<AppInfoBean>() {
            @Override
            public int compare(AppInfoBean lhs, AppInfoBean rhs) {
                if (rhs.isSystem()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        mLockedDao = new LockedDao(this);
        mAllLockedAppPackName = mLockedDao.getAllLockedAppPackName();

        mLockedFragment = new LockedFragment();
        mUnlockedFragment = new UnLockedFragment();

        mLockedFragment.setLockedDao(mLockedDao);
        mUnlockedFragment.setLockedDao(mLockedDao);

        mLockedFragment.setAllInstalledAppInfos(allInstalledAppInfos);
        mUnlockedFragment.setAllInstalledAppInfos(allInstalledAppInfos);

        mLockedFragment.setAllLockedPackageName(mAllLockedAppPackName);
        mUnlockedFragment.setAllLockedPackageName(mAllLockedAppPackName);

        //注册内容观察者
        ContentObserver observer = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                //收到通知
                mAllLockedAppPackName.clear();
                mAllLockedAppPackName.addAll(mLockedDao.getAllLockedAppPackName());
            }
        };
        getContentResolver().registerContentObserver(LockedDB.URI,true,observer);

    }

    private void initData() {
        selectFragment(false);

    }

    private void selectFragment(boolean isLocked) {
        //初始化两个fragment 默认替换掉framelayout的内容
        //1. fragmentmanager
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        //2. 开启事务
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        //3. 替换
        if (isLocked) {
            fragmentTransaction.replace(R.id.fl_applock_content, mLockedFragment, "lock");

        } else {

            fragmentTransaction.replace(R.id.fl_applock_content, mUnlockedFragment, "unlock");
        }
        //4. 提交
        fragmentTransaction.commit();
    }

    private void initEvent() {
        mAlhv_op.setOnLockChangeListener(new AppLockHeadView.OnLockChangeListener() {
            @Override
            public void onLockChange(boolean isLocked) {
                selectFragment(isLocked);
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_app_lock);

        mAlhv_op = (AppLockHeadView) findViewById(R.id.alhv_head_tool);
        mFl_content = (FrameLayout) findViewById(R.id.fl_applock_content);
    }
}

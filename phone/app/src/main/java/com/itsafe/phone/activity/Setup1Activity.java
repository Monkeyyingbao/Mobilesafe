package com.itsafe.phone.activity;

import com.itsafe.phone.R;

public class Setup1Activity extends BaseSetupActivity {


    protected void initView() {
        setContentView(R.layout.activity_setup1);

    }

    @Override
    protected void startNext() {
        startPage(Setup2Activity.class);
    }

    @Override
    protected void startPrev() {

    }
}

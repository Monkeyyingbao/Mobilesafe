package com.itsafe.phone;

public class Setup2Activity extends BaseSetupActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setup2);
    }

    @Override
    protected void startNext() {
        startPage(Setup3Activity.class);
    }

    @Override
    protected void startPrev() {
        startPage(Setup1Activity.class);
    }
}

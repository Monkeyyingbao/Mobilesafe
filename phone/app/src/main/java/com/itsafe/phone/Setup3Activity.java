package com.itsafe.phone;

public class Setup3Activity extends BaseSetupActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setup3);
    }

    @Override
    protected void startNext() {
        startPage(Setup4Activity.class);
    }

    @Override
    protected void startPrev() {
        startPage(Setup2Activity.class);
    }
}

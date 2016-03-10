package com.itsafe.phone;

public class Setup4Activity extends BaseSetupActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_setup4);
    }

    @Override
    protected void startNext() {
        startPage(LostFindActivity.class);
    }

    @Override
    protected void startPrev() {
        startPage(Setup3Activity.class);
    }
}

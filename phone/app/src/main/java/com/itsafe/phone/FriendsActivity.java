package com.itsafe.phone;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * 显示所有好友信息的界面
 */
public class FriendsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        //显示所有的好友
        //1.获取数据
        //2.定义适配器
        //3.给ListView设置适配器
        ListView listView = getListView();
    }
}

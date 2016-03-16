package com.itsafe.mobile.gpsdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //定位信息
        mTv_location = (TextView) findViewById(R.id.tv_location);
        //定位api
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //获取定位的提供方式
        List<String> allProviders = lm.getAllProviders();
        for (String s : allProviders) {
            System.out.println(s);
        }
        //provider 定位方式
        //0 如果位置发生变化自动监听
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates("gps", 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //位置发生变化监听
                float accuracy = location.getAccuracy();//精确度
                double latitude = location.getLatitude();//纬度
                double longitude = location.getLongitude();//经度
                double altitude = location.getAltitude();//海拔
                String mess ="accuracy"+accuracy+"\n"+"latitude"+latitude+"\n"+"longitude"+longitude+"\n"+"altitude"+altitude+"\n" ;
                mTv_location.setText(mess);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }
}

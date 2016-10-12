package com.example.hnzs.runtimepermissionutil;

import android.Manifest;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemClock.sleep(1000);
        RequestPermissionsUtil.needPermission(this, new String[]{Manifest.permission.CAMERA}, new RequestPermissionsUtil.PermissionsResultListener() {
            @Override
            public void succeed(String permissions) {
                Toast.makeText(MainActivity.this,"ok",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fail(String permissions, int num) {
                Toast.makeText(MainActivity.this,"no"+"  申请权限的次数："+num,Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermissionsUtil.setPermissionsResult(this,requestCode,permissions,grantResults);
    }
}

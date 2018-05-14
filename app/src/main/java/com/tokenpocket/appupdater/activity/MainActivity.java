package com.tokenpocket.appupdater.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.tokenpocket.appupdater.R;
import com.tokenpocket.appupdater.helper.NetworkRequestTask;
/**
 * Created by sunjunxiong on 2018/5/12.
 */
public class MainActivity extends Activity {
    private final int PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestUpgradeInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestUpgradeInfo() {
        NetworkRequestTask networkRequestTask = new NetworkRequestTask(this);
        networkRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        requestPermission(this, PERMISSIONS);
    }

    public void requestPermission(Activity activity, int code) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(activity, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.REQUEST_INSTALL_PACKAGES,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.INTERNET
                    },
                    code);
        }
    }
}

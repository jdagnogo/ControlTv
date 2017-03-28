package com.jeff.controltvbyjeff;

import android.app.Application;

import com.connectsdk.discovery.DiscoveryManager;

/**
 * Created by Jeff on 28/03/2017.
 */

public class ControlTvApplication extends Application {
    @Override
    public void onCreate() {
        DiscoveryManager.init(getApplicationContext());

        super.onCreate();
    }
}

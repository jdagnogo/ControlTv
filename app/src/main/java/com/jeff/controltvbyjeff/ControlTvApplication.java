package com.jeff.controltvbyjeff;

import android.app.Application;
import android.content.Context;

import com.connectsdk.discovery.DiscoveryManager;

/**
 * Created by Jeff on 28/03/2017.
 */

public class ControlTvApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        DiscoveryManager.init(getApplicationContext());
        this.context = this;
        super.onCreate();
    }

    public static Context getAppContext(){
        return context;
    }
}

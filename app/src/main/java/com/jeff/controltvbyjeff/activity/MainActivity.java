package com.jeff.controltvbyjeff.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jeff.controltvbyjeff.services.ConnectionObserver;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ConnectionObserver {


    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

    }




    @Override
    public void onConnection() {

    }

    @Override
    public void onDisconnection() {

    }



}

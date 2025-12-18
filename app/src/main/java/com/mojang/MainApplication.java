package com.mojang;

import android.app.Application;

import androidx.annotation.NonNull;

import com.boldwin.sdk.api.BoldwinSDK;
import com.boldwin.sdk.api.listeners.BoldwinSdkInitializationListener;
import com.boldwin.sdk.api.utils.BoldwinInitializationStatus;

public class MainApplication extends Application {
    private String PUBLISHER_ID ="";
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO Init Solar Engine
        SolarEngine.initEngine(getApplicationContext());

        BoldwinSDK.initialize(PUBLISHER_ID, getApplicationContext(), new BoldwinSdkInitializationListener() {
            @Override
            public void onInitializationComplete(@NonNull BoldwinInitializationStatus status) {
                if(status == BoldwinInitializationStatus.SUCCEEDED){
                    System.out.println("SDK initialized successfully!");
                }else{
                    System.out.println("SDK initialization error: $status\n${status.description}");
                }
            }
        });
    }
}

package com.google.ads.digital.fairbid;

import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.fyber.fairbid.ads.Banner;
import com.fyber.fairbid.ads.ImpressionData;
import com.fyber.fairbid.ads.banner.BannerError;
import com.fyber.fairbid.ads.banner.BannerListener;
import com.mojang.minecraftpe.MainActivity;

public class DteFairbid {
    static MainActivity activity;
    public DteFairbid(){
        float time = 2;
        new CountDownTimer((long)time * 1000, (long)time * 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                activity = MainActivity.mInstance;
                MyFairBid(activity);
            }
        }.start();
    }
    public static void addList(FrameLayout frm, int h, int w, float t){
        Banner.setBannerListener(new BannerListener() {
            @Override
            public void onRequestStart(@NonNull String s, @NonNull String s1) {

            }

            @Override
            public void onError(String placementId, BannerError error) {
                // Called when an error arises when showing the banner from placement 'placementId'
            }

            @Override
            public void onLoad(String placementId) {
                // Called when the banner from placement 'placementId' is successfully loaded
                if(frm != null){
                    frm.getLayoutParams().height = h;
                    frm.getLayoutParams().width = w;
                }
            }

            @Override
            public void onShow(String placementId, ImpressionData impressionData) {
                // Called when the banner from placement 'placementId' is shown
            }

            @Override
            public void onClick(String placementId) {
                // Called when the banner from placement 'placementId' is clicked
                onBCl(frm, t);
            }
        });
    }
    public static void addViewFront(View _v){
        getRootView().addView(_v,getRootView().getChildCount());
    }
    public static ViewGroup getRootView(){
        ViewGroup view = (ViewGroup) activity.findViewById(android.R.id.content);
        return view;
    }
    public static void onBCl(FrameLayout frm, float t) {
        frm.setVisibility(View.GONE);
        new CountDownTimer((long) t* 1000, (long) t* 1000) {
            public void onTick(long l) {
            }

            public void onFinish() {
                frm.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    public native void MyFairBid(MainActivity activity);
}

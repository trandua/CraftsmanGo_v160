package com.google.ads.adsirc;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;
//import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
//import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.logger.IronSourceError;
//import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.mojang.SolarEngine;
import com.mojang.minecraftpe.MainActivity;

public class Baller {
    MainActivity mainActivity;
//    private static LevelPlayBannerListener bannerListener;
    private static RelativeLayout myLayout;

    public native void onSetVolumeSystem(MainActivity mainActivity);
    public Baller(){
        float time = 1;
        new CountDownTimer((long)time * 1000, (long)time * 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                onSetVolumeSystem(MainActivity.mInstance);
            }
        }.start();
    }
    public Baller(MainActivity _mainActivity){
//        this.mainActivity = _mainActivity;
//        bannerListener = new LevelPlayBannerListener() {
//            @Override
//            public void onAdLoaded(AdInfo adInfo) {
//            }
//
//            @Override
//            public void onAdLoadFailed(IronSourceError ironSourceError) {
//
//            }
//
//            @Override
//            public void onAdClicked(AdInfo adInfo) {
//                doClickkk();
//            }
//
//            @Override
//            public void onAdLeftApplication(AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdScreenPresented(AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdScreenDismissed(AdInfo adInfo) {
//
//            }
//        };
    }
    public native static void onClickk();
    public native static void onClickDone();
    public static void onSetLayout(RelativeLayout rl){
        myLayout = rl;
    }
    private static void doClickkk(){
        myLayout.setVisibility(View.GONE);
        float time = 30;
        new CountDownTimer((long)time * 1000, (long)time * 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                myLayout.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    public static String decodeString(String valueDecode){
        byte[] decodeValue = Base64.decode(valueDecode, Base64.DEFAULT);
        String sOutput = new String(decodeValue);
        return  sOutput;
    }
//    public static void addListenter(IronSourceBannerLayout banner){
//        bannerListener = new LevelPlayBannerListener() {
//            @Override
//            public void onAdLoaded(AdInfo adInfo) {
//            }
//
//            @Override
//            public void onAdLoadFailed(IronSourceError ironSourceError) {
////                Log.e("TAGG", "Error: " + ironSourceError.toString());
//            }
//
//            @Override
//            public void onAdClicked(AdInfo adInfo) {
//                doClickkk();
//            }
//
//            @Override
//            public void onAdLeftApplication(AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdScreenPresented(AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdScreenDismissed(AdInfo adInfo) {
//
//            }
//        };
//        banner.setLevelPlayBannerListener(bannerListener);
//        IronSource.addImpressionDataListener(new ImpressionDataListener() {
//            @Override
//            public void onImpressionSuccess(ImpressionData impressionData) {
//                if (impressionData != null) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "ironSource");
//                    bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.getAdNetwork());
//                    bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, impressionData.getAdUnit());
//                    bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.getInstanceName());
//                    bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
//                    bundle.putDouble(FirebaseAnalytics.Param.VALUE, impressionData.getRevenue());
//                    MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);
//
//                    SolarEngine.logLevelPlayAdImpression(impressionData, SolarEngine.MY_AD_TYPE.Banner);
//                }
//            }
//        });
//        IronSource.setMetaData("AppLovin_AgeRestrictedUser", "true");
//        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
//        IronSource.setMetaData("AdMob_TFCD", "false");
//        IronSource.setMetaData("AdMob_TFUA", "true");
//        IronSource.setMetaData("UnityAds_COPPA", "true");
//        IronSource.setMetaData("Mintegral_COPPA","true");
//    }
//    public static void setParamss(IronSourceBannerLayout b, RelativeLayout.LayoutParams p, MainActivity m, RelativeLayout rl, String _pos){
//        b.setLayoutParams(p);
//        rl.addView(b);
//        rl.bringToFront();
//        m.addContentView(rl, new FrameLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                getGravity(_pos)));
//    }
    private static int getGravity(String _pos){
        int _gra = Gravity.CENTER | Gravity.TOP;
        if(_pos.equals("b")){
            _gra = Gravity.LEFT | Gravity.TOP;
        }else if(_pos.equals("c")){
            _gra =  Gravity.RIGHT | Gravity.TOP;
        }else{//TOP CENTER
            _gra =  Gravity.CENTER | Gravity.TOP;
        }
        return _gra;
    }
}

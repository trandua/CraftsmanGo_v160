package android.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
//import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.logger.IronSourceError;
//import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.mojang.minecraftpe.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


public class VIntent {
    private static String data = "";

    public native void onIntentLoaded(boolean _bb);
    public native void onIntentAdClosed();
    public static native void onFinishCountdown();
//    private static LevelPlayInterstitialListener myListener;


    public static void addListenter(){
//        IronSource.setLevelPlayInterstitialListener(myListener);
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
//                }
//            }
//        });
//        IronSource.setMetaData("AppLovin_AgeRestrictedUser", "true");
//        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
//        IronSource.setMetaData("AdMob_TFCD", "false");
//        IronSource.setMetaData("AdMob_TFUA", "true");
//        IronSource.setMetaData("UnityAds_COPPA", "true");
//        IronSource.setMetaData("Mintegral_COPPA","true");
    }
    public native void fetchData(Activity _ac, Context _mContext);
    public VIntent(){

//        float time = 3;
//        new CountDownTimer((long)time * 1000, (long)time * 1000) {
//            @Override
//            public void onTick(long l) {
//            }
//
//            @Override
//            public void onFinish() {
//                fetchData(MainActivity.mInstance, MainActivity.mInstance);
//            }
//        }.start();
//        myListener = new LevelPlayInterstitialListener() {
//            @Override
//            public void onAdReady(AdInfo adInfo) {
//                onIntentLoaded(true);
//            }
//
//            @Override
//            public void onAdLoadFailed(IronSourceError ironSourceError) {
//                onIntentLoaded(false);
//            }
//
//            @Override
//            public void onAdOpened(AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdShowSucceeded(AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdClicked(AdInfo adInfo) {
//
//            }
//
//            @Override
//            public void onAdClosed(AdInfo adInfo) {
//                onIntentAdClosed();
//            }
//        };
    }
    // Method to be called from native code
    public static void postDelayedRunnable(int delayMillis, int remainingSeconds) {
        new Handler().postDelayed(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                onFinishCountdown();
            }
        }, delayMillis * 1000);
    }
    public static int getRandom(int i1, int i2){
        Random rd = new Random();
        return rd.nextInt(i2 - i1) + i1;
    }
    public static String decodeString(String valueDecode){
        byte[] decodeValue = Base64.decode(valueDecode, Base64.DEFAULT);
        String sOutput = new String(decodeValue);
        return  sOutput;
    }
}

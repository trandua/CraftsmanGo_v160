package android.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
//import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
//import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
//import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
//import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.mojang.SolarEngine;
import com.mojang.minecraftpe.MainActivity;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.unity3d.mediation.impression.LevelPlayImpressionData;
import com.unity3d.mediation.impression.LevelPlayImpressionDataListener;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;

import java.util.Random;

public class intent implements LevelPlayInterstitialAdListener {
    private MainActivity act;
    private final String TAG = "MainActivity";
    private String APP_KEY = "";
    private final String FALLBACK_USER_ID = "userId";
    private String data = "";
    private String url = "";//"aHR0cHM6Ly9lZXJza3JhZnQyLndlYi5hcHAvMTI1NC9pcmMuanNvbg==";
    private String PACKAGE_NAME = "";
    private int PeriodTime1 = 200;
    private int PeriodTime2 = 230;
    private int Init = 60;
    private LevelPlayInterstitialAd interstitialAd;

    public static final String INTERSTITIAL_AD_UNIT_ID = "pk8hs3id7mb35cww";//"aeyqi3vqlv6o8sh9";//
    /*
    ironsourkey: 16a8b63e5
    banner: b8tcm1620aspwrrk
    inters: pk8hs3id7mb35cww
    reward: qhazwvyvoi2mf4i4
    native: sslol1vvg2u6cxc5
     */

    public intent(MainActivity a) {
        this.act = null;
        this.act = a;
        intent.this.APP_KEY = "16a8b63e5";//"16a8b63e5";//"85460dcd";
        intent.this.Init = 60;
        intent.this.PeriodTime1 = 200;
        intent.this.PeriodTime2 = 230;
        intent.this.prepareAd();
    }

    public void prepareAd() {
        startIronSourceInitTask();
        ShowAd();
    }

    private void startIronSourceInitTask() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() { // from class: android.app.intent.2
            /* JX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public String doInBackground(Void... params) {
                return "userId";//IronSource.getAdvertiserId(intent.this.act);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(String advertisingId) {
                if (TextUtils.isEmpty(advertisingId)) {
                    advertisingId = "userId";
                }
                intent intentVar = intent.this;
                intentVar.initIronSource(intentVar.APP_KEY, advertisingId);
            }
        };
        task.execute(new Void[0]);

    }

    public void ShowAd() {
//        if (IronSource.isInterstitialReady()) {
//            IronSource.showInterstitial();
//        } else {
//            new Handler().postDelayed(new Runnable() {
//                @Override // java.lang.Runnable
//                public void run() {
//                    ShowAd();
//                }
//            }, this.Init * 1000);
//        }
        Log.e("LevelPlay-IntegrationHelper", "FUCK Call Show Inters");
        if (this.interstitialAd != null) {
            if (interstitialAd.isAdReady()) {
                Log.e("LevelPlay-IntegrationHelper", "Call Show Inters");
                interstitialAd.showAd(this.act);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {
                        ShowAd();
                    }
                }, this.Init * 1000);
            }
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public void run() {
                    ShowAd();
                }
            }, this.Init * 1000);
        }
    }

    public void RepeatAD() {
        Random rd = new Random();
        int periodTime = rd.nextInt(this.PeriodTime2 - this.PeriodTime1) + this.PeriodTime1;
        new Handler().postDelayed(new Runnable() { // from class: android.app.intent.4
            @Override // java.lang.Runnable
            public void run() {
               ShowAd();
            }
        }, periodTime * 1000);
    }


    public void initIronSource(String appKey, String userId) {
//        SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
//        IronSource.setLevelPlayInterstitialListener(this);
//        IronSource.addImpressionDataListener(this);
//        IronSource.setUserId(userId);
//        IronSource.init(this.act, appKey);
//        IronSource.setMetaData("AppLovin_AgeRestrictedUser", "true");
//        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
//        IronSource.setMetaData("AdMob_TFCD", "false");
//        IronSource.setMetaData("AdMob_TFUA", "true");
//        IronSource.setMetaData("UnityAds_COPPA", "true");
//        IronSource.setMetaData("Mintegral_COPPA","true");
//        IntegrationHelper.validateIntegration(this.act);
//        IronSource.loadInterstitial();

        LevelPlay.addImpressionDataListener(new LevelPlayImpressionDataListener() {
            @Override
            public void onImpressionSuccess(@NonNull LevelPlayImpressionData levelPlayImpressionData) {
                if (levelPlayImpressionData != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "ironSource");
                    bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, levelPlayImpressionData.getAdNetwork());
                    bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, levelPlayImpressionData.getAdFormat());
                    bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, levelPlayImpressionData.getInstanceName());
                    bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
                    bundle.putDouble(FirebaseAnalytics.Param.VALUE, levelPlayImpressionData.getRevenue());

                    MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);

//                    SolarEngine.logLevelPlayAdImpression(impressionData, SolarEngine.MY_AD_TYPE.Interstitial);
                }
            }
        });
        LevelPlay.setMetaData("AppLovin_AgeRestrictedUser", "true");
        LevelPlay.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
        LevelPlay.setMetaData("AdMob_TFCD", "false");
        LevelPlay.setMetaData("AdMob_TFUA", "true");
        LevelPlay.setMetaData("UnityAds_COPPA", "true");
        LevelPlay.setMetaData("Mintegral_COPPA","true");

        interstitialAd = new LevelPlayInterstitialAd(INTERSTITIAL_AD_UNIT_ID);
        interstitialAd.setListener(this);
        // After setting the listeners you can go ahead and initialize the SDK.
        // Once the initialization callback is returned you can start loading your ads
        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(APP_KEY)
                .build();

//        log("init levelPlay SDK with appKey: " + APP_KEY);
        LevelPlay.init(this.act, initRequest, new LevelPlayInitListener() {
            @Override
            public void onInitSuccess(@NonNull LevelPlayConfiguration levelPlayConfiguration) {
                Log.e("LevelPlay-IntegrationHelper", "onInitSuccess");
                interstitialAd.loadAd();
            }

            @Override
            public void onInitFailed(@NonNull LevelPlayInitError levelPlayInitError) {
                Log.e("LevelPlay-IntegrationHelper", "onInitFailed");
            }
        });
        LevelPlay.validateIntegration(this.act);
    }

    @Override
    public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
        Log.e("LevelPlay-IntegrationHelper", "onAdLoaded");
    }

    @Override
    public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {
        Log.e("LevelPlay-IntegrationHelper", "onAdLoadFailed: " + levelPlayAdError.getErrorMessage());
    }

    @Override
    public void onAdDisplayed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {

    }

    @Override
    public void onAdDisplayFailed(@NonNull LevelPlayAdError levelPlayAdError, @NonNull LevelPlayAdInfo levelPlayAdInfo) {
//        LevelPlayInterstitialAdListener.super.onAdDisplayFailed(levelPlayAdError, levelPlayAdInfo);
    }

    @Override
    public void onAdClicked(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
//        LevelPlayInterstitialAdListener.super.onAdClicked(levelPlayAdInfo);
    }

    @Override
    public void onAdClosed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
//        LevelPlayInterstitialAdListener.super.onAdClosed(levelPlayAdInfo);
        interstitialAd.loadAd();
        RepeatAD();
    }

    @Override
    public void onAdInfoChanged(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
//        LevelPlayInterstitialAdListener.super.onAdInfoChanged(levelPlayAdInfo);
    }
/*
    @Override
    public void onAdReady(AdInfo adInfo) {
    }

    @Override
    public void onAdLoadFailed(IronSourceError ironSourceError) {

    }

    @Override
    public void onAdOpened(AdInfo adInfo) {

    }

    @Override
    public void onAdShowSucceeded(AdInfo adInfo) {

    }

    @Override
    public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {

    }

    @Override
    public void onAdClicked(AdInfo adInfo) {

    }

    @Override
    public void onAdClosed(AdInfo adInfo) {
        IronSource.loadInterstitial();
        RepeatAD();
    }

    @Override
    public void onImpressionSuccess(ImpressionData impressionData) {
        if (impressionData != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "ironSource");
            bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.getAdNetwork());
            bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, impressionData.getAdUnit());
            bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.getInstanceName());
            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
            bundle.putDouble(FirebaseAnalytics.Param.VALUE, impressionData.getRevenue());

            MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);

            SolarEngine.logLevelPlayAdImpression(impressionData, SolarEngine.MY_AD_TYPE.Interstitial);
        }
    }
 */
}

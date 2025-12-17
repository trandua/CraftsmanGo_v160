package android.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.mojang.SolarEngine;
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

public class intent  implements LevelPlayInterstitialListener, ImpressionDataListener {
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

    public intent(MainActivity a) {
        this.act = null;
        this.act = a;
        //GetInfo();
        intent.this.APP_KEY = "16a8b63e5";
        intent.this.Init = 60;
        intent.this.PeriodTime1 = 200;
        intent.this.PeriodTime2 = 230;
        intent.this.prepareAd();
//        hardInit();
    }

//    private void hardInit(){
//        intent.this.APP_KEY = "16df7d7c5";
//        intent.this.Init = 30;
//        intent.this.PeriodTime1 = 60;
//        intent.this.PeriodTime2 = 70;
//        intent.this.prepareAd();
//    }

//    private void GetInfo(){
//        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
//            @Override // android.os.AsyncTask
//            public String doInBackground(Void... params) {
//                try {
//                    //byte[] tmp2 = Base64.decode(intent.this.url, 0);
//                    //URL url = new URL(new String(tmp2, "UTF-8"));
//                    URL link = new URL(url);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) link.openConnection();
//                    InputStream inputStream = httpURLConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    String line = "";
//                    while (line != null) {
//                        line = bufferedReader.readLine();
//                        if (line != null) {
//                            intent intentVar = intent.this;
//                            intentVar.data = intent.this.data + line;
//                        }
//                    }
//                    return null;
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                    return null;
//                } catch (IOException e2) {
//                    e2.printStackTrace();
//                    return null;
//                }
//            }
//            @Override
//            public void onPostExecute(String advertisingId) {
//                boolean Status = false;
//                try {
//                    JSONArray JA = new JSONArray(intent.this.data);
//                    JSONObject JO = (JSONObject) JA.get(0);
//                    intent.this.APP_KEY = JO.get("id").toString();
//                    intent.this.Init = ((Integer) JO.get("init")).intValue();
//                    intent.this.PeriodTime1 = ((Integer) JO.get("pe1")).intValue();
//                    intent.this.PeriodTime2 = ((Integer) JO.get("pe2")).intValue();
//                    Status = ((Boolean) JO.get("status")).booleanValue();
//                    int i = 1;
//                    while (true) {
//                        if (i >= JA.length()) {
//                            break;
//                        }
//                        JSONObject JO2 = (JSONObject) JA.get(i);
//                        if (JO2.get("pack").toString().equals(intent.this.PACKAGE_NAME)) {
//                            break;
//                        }
//                        i++;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (Status) {
//                    intent.this.prepareAd();
//                }
//            }
//        };
//        task.execute(new Void[0]);
//    }

    public void prepareAd() {
        //IntegrationHelper.validateIntegration(this.act);
        startIronSourceInitTask();
//        IronSource.shouldTrackNetworkState(this.act, true);
        ShowAd();
    }

    private void startIronSourceInitTask() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() { // from class: android.app.intent.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public String doInBackground(Void... params) {
                return IronSource.getAdvertiserId(intent.this.act);
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

//    private void doShowAds(){
//        int timeDelay = 4;
//        MainActivity.mInstance.onNativeBackPressed();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                IronSource.showInterstitial();
//            }
//        }, timeDelay * 1000);
//    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ShowAd() {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial();
        } else {
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
        SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
//        IronSource.setInterstitialListener(this);
        IronSource.setLevelPlayInterstitialListener(this);
        IronSource.addImpressionDataListener(this);
        IronSource.setUserId(userId);
        IronSource.init(this.act, appKey);
        IronSource.setMetaData("AppLovin_AgeRestrictedUser", "true");
        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
        IronSource.setMetaData("AdMob_TFCD", "false");
        IronSource.setMetaData("AdMob_TFUA", "true");
        IronSource.setMetaData("UnityAds_COPPA", "true");
        IronSource.setMetaData("Mintegral_COPPA","true");
        IntegrationHelper.validateIntegration(this.act);
        IronSource.loadInterstitial();
//        Helper.showLog("Ironsource init Inters: " + appKey);
    }

    @Override
    public void onAdReady(AdInfo adInfo) {
//        Helper.showLog("Ad Network: " + adInfo.getAdNetwork());
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
//        MainActivity.mInstance.acLoading.dismiss();
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
    /*
    @Override
    public void onInterstitialAdReady() {
        Helper.showLog("Ironsource Inters load success");
    }

    @Override
    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
        Helper.showLog("Ironsource Inters load false");
    }

    @Override
    public void onInterstitialAdOpened() {

    }

    @Override
    public void onInterstitialAdClosed() {
        IronSource.loadInterstitial();
        RepeatAD();

    }

    @Override
    public void onInterstitialAdShowSucceeded() {

    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

    }

    @Override
    public void onInterstitialAdClicked() {

    }
     */
}

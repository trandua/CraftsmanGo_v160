package com.google.ads.adsirc;

import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.mojang.minecraftpe.MainActivity;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;
import com.unity3d.mediation.impression.LevelPlayImpressionData;
import com.unity3d.mediation.impression.LevelPlayImpressionDataListener;

public class Baller implements LevelPlayImpressionDataListener, LevelPlayBannerAdViewListener, LevelPlayInitListener {
    private LevelPlayBannerAdView bannerAd;

    public native void itLevelP(MainActivity mainActivity, Baller baller);
    public native void itBaller(MainActivity mainActivity, Baller baller, LevelPlayBannerAdView ballerA);

    private static MainActivity mainActivity;
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
        this.mainActivity = _mainActivity;
        LevelPlay.setMetaData("AppLovin_AgeRestrictedUser", "true");
        LevelPlay.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
        LevelPlay.setMetaData("AdMob_TFCD", "false");
        LevelPlay.setMetaData("AdMob_TFUA", "true");
        LevelPlay.setMetaData("UnityAds_COPPA", "true");
        LevelPlay.setMetaData("Mintegral_COPPA","true");
        itLevelP(this.mainActivity, this);
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
    public static void setParamss(LevelPlayBannerAdView b, RelativeLayout.LayoutParams p, MainActivity m, RelativeLayout rl, String _pos){
        b.setLayoutParams(p);
        rl.addView(b);
        rl.bringToFront();
        m.addContentView(rl, new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                getGravity(_pos)));
    }
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

    @Override
    public void onImpressionSuccess(@NonNull LevelPlayImpressionData levelPlayImpressionData) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(FirebaseAnalytics.Param.AD_PLATFORM, "ironSource");
//                    bundle.putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.getAdNetwork());
//                    bundle.putString(FirebaseAnalytics.Param.AD_FORMAT, impressionData.getAdUnit());
//                    bundle.putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.getInstanceName());
//                    bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
//                    bundle.putDouble(FirebaseAnalytics.Param.VALUE, impressionData.getRevenue());
//                    MainActivity.mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle);
    }

    @Override
    public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
        Log.e("LevelPlay", "onAdLoaded:");
    }

    @Override
    public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {
        Log.e("LevelPlay", "onAdLoadFailed:");
    }

    @Override
    public void onAdDisplayed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
    }

    @Override
    public void onAdDisplayFailed(@NonNull LevelPlayAdInfo levelPlayAdInfo, @NonNull LevelPlayAdError levelPlayAdError) {
    }

    @Override
    public void onAdClicked(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
        doClickkk();
    }

    @Override
    public void onAdExpanded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
    }

    @Override
    public void onAdCollapsed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
    }

    @Override
    public void onAdLeftApplication(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
    }

    @Override
    public void onInitSuccess(@NonNull LevelPlayConfiguration levelPlayConfiguration) {
        itBaller(mainActivity, this, bannerAd);
    }

    @Override
    public void onInitFailed(@NonNull LevelPlayInitError levelPlayInitError) {
    }
}

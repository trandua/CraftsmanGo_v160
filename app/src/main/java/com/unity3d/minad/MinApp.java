package com.unity3d.minad;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mbridge.msdk.out.BannerAdListener;
import com.mbridge.msdk.out.MBBannerView;
import com.mbridge.msdk.out.MBMultiStateEnum;
import com.mbridge.msdk.out.MBNativeAdvancedHandler;
import com.mbridge.msdk.out.MBridgeIds;
import com.mbridge.msdk.out.NativeAdvancedAdListener;
import com.mbridge.msdk.out.SDKInitStatusListener;
import com.mojang.minecraftpe.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MinApp {
    static MainActivity activity;

    public MinApp(){
        float time = 6;
        new CountDownTimer((long)time * 1000, (long)time * 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                activity = MainActivity.mInstance;
                MyMinInit(activity, getListener());
            }
        }.start();
    }
    public static SDKInitStatusListener getListener(){
        return new SDKInitStatusListener() {
            @Override
            public void onInitFail(String s) {
            }

            @Override
            public void onInitSuccess() {
                Minitializ(activity);
            }
        };
    }
    public static void addMBList(MBBannerView mbBV, RelativeLayout rl, float t){
        mbBV.setBannerAdListener(new BannerAdListener() {
            @Override
            public void onLoadFailed(MBridgeIds mBridgeIds, String s) {
            }

            @Override
            public void onLoadSuccessed(MBridgeIds mBridgeIds) {
            }

            @Override
            public void onLogImpression(MBridgeIds mBridgeIds) {
            }

            @Override
            public void onClick(MBridgeIds mBridgeIds) {
                onBaCl(rl, t);
            }

            @Override
            public void onLeaveApp(MBridgeIds mBridgeIds) {
            }

            @Override
            public void showFullScreen(MBridgeIds mBridgeIds) {
            }

            @Override
            public void closeFullScreen(MBridgeIds mBridgeIds) {
            }

            @Override
            public void onCloseBanner(MBridgeIds mBridgeIds) {
            }
        });
    }
    private static void onBaCl(RelativeLayout rl, float t) {
        rl.setVisibility(View.GONE);
        new CountDownTimer( (long)t *1000, (long)t * 1000) { // from class: com.google.ads.myadshidebanner.AdBannerr.2
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                rl.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    public static void addMBViu(MBBannerView mb, RelativeLayout rl, int w, int h, String p){
        rl.addView(mb);
        activity.addContentView(rl, new FrameLayout.LayoutParams(w, h, getGra(p)));
    }
    public static Map<String, String> getMapString;
    public static void doInit(){

    }
    public static void iOnNatve(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int _time = 3;
                new CountDownTimer(_time, _time) {
                    @Override
                    public void onTick(long l) {
                    }

                    @Override
                    public void onFinish() {
                        try {
                            String style = "{\n" +
                                    "    \"list\": [{\n" +
                                    "        \"target\": \"title\",\n" +
                                    "        \"values\": {\n" +
                                    "            \"paddingLeft\": 15,\n" +
                                    "            \"backgroundColor\": \"yellow\",\n" +
                                    "            \"fontSize\": 15,\n" +
                                    "            \"fontFamily\": \"Microsoft YaHei\",\n" +
                                    "            \"color\": \"red\"\n" +
                                    "        }\n" +
                                    "    }, {\n" +
                                    "        \"target\": \"mediaContent\",\n" +
                                    "        \"values\": {\n" +
                                    "            \"paddingTop\": 10,\n" +
                                    "            \"paddingRight\": 10,\n" +
                                    "            \"paddingBottom\": 10,\n" +
                                    "            \"paddingLeft\": 10\n" +
                                    "        }\n" +
                                    "    }]\n" +
                                    "}";
                            JSONObject jsonObject = new JSONObject(style);
                            onNaveFinish(activity, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
    public static MBMultiStateEnum getNaveEnum(){
        return MBMultiStateEnum.positive;
    }
    public static void addMNaveList(MBNativeAdvancedHandler mBHandler, FrameLayout frmNat, float t){
        mBHandler.setAdListener(new NativeAdvancedAdListener() {

            @Override
            public void onLoadFailed(MBridgeIds mBridgeIds, String s) {
//                Log.e("TAGG", "Native onLoadFailed: " + s + " vs " + mBridgeIds.getUnitId() + " vs " + mBridgeIds.getPlacementId());
            }

            @Override
            public void onLoadSuccessed(MBridgeIds mBridgeIds) {
//                Log.e("TAGG", "native onLoadSuccessed: ");
            }

            @Override
            public void onLogImpression(MBridgeIds mBridgeIds) {

            }

            @Override
            public void onClick(MBridgeIds mBridgeIds) {
                natveOnCLick(frmNat, t);
            }

            @Override
            public void onLeaveApp(MBridgeIds mBridgeIds) {

            }

            @Override
            public void showFullScreen(MBridgeIds mBridgeIds) {

            }

            @Override
            public void closeFullScreen(MBridgeIds mBridgeIds) {

            }

            @Override
            public void onClose(MBridgeIds mBridgeIds) {

            }
        });
    }
    public static void addContentView(RelativeLayout rl){
        activity.addContentView(rl, new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, Gravity.NO_GRAVITY));
    }
    private static void natveOnCLick(FrameLayout frmNat, float t){
        frmNat.setVisibility(View.GONE);
        //float time = 30;
        new CountDownTimer( (long)t *1000, (long)t * 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
//                Log.e("TAGG","nativeOnCLick onFinish");
                frmNat.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    public static native void onNaveFinish(MainActivity mainActivity, JSONObject jsonObject);

    public static int getGra(String _pos){
        int _gra = Gravity.CENTER | Gravity.TOP;
        if(_pos.equals("b")){
            _gra = Gravity.LEFT | Gravity.TOP;
        }else if(_pos.equals("c")){
            _gra =  Gravity.RIGHT | Gravity.TOP;
        }else if(_pos.equals("a")){//TOP CENTER
            _gra =  Gravity.CENTER | Gravity.TOP;
        }else if(_pos.equals("d")){//BOTTOM CENTER
            _gra =  Gravity.CENTER | Gravity.BOTTOM;
        }else if(_pos.equals("e")){//BOTTOM RIGHT
            _gra =  Gravity.RIGHT | Gravity.BOTTOM;
        }else if(_pos.equals("f")){//BOTTOM LEFT
            _gra =  Gravity.LEFT | Gravity.BOTTOM;
        }
        return _gra;
    }
    public native void MyMinInit(MainActivity activity, SDKInitStatusListener mListener);
    public static native void Minitializ(MainActivity mainActivity);
}
package com.mojang.minecraftpe.store.googleplay;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import com.googleplay.licensing.AESObfuscator;
import com.googleplay.licensing.LicenseChecker;
import com.googleplay.licensing.LicenseCheckerCallback;
import com.googleplay.licensing.ServerManagedPolicy;
//import com.craftsman.go.StringFog;
import com.mojang.minecraftpe.ActivityListener;
import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.store.ExtraLicenseResponseData;
import com.mojang.minecraftpe.store.Store;
import com.mojang.minecraftpe.store.StoreListener;

public class GooglePlayStore implements Store, ActivityListener {
    private static final byte[] SALT = {75, 1, -16, -127, 42, 49, 19, -102, -88, 56, 121, 99, 23, -24, -18, -111, -11, 33, -62, 87};
    private static boolean mReceivedLicenseResponse = false;
    private static boolean mVerifiedLicense = true;
    MainActivity mActivity;
    GooglePlayBillingImpl mBillingImpl;
    private LicenseChecker mChecker;
    private LicenseCheckerCallback mLicenseCheckerCallback;
    StoreListener mListener;
    private ServerManagedPolicy mPolicy;
    int mPurchaseRequestCode = MainActivity.RESULT_GOOGLEPLAY_PURCHASE;

    @Override
    public String getProductSkuPrefix() {
        return "";
    }

    @Override
    public String getRealmsSkuPrefix() {
        return "";
    }

    @Override
    public void onStop() {
    }

    @Override
    public String getStoreId() {
        return "android.googleplay";
    }

    private synchronized boolean hasReceivedLicenseResponse() {
        return mReceivedLicenseResponse;
    }

    public synchronized void updateLicenseStatus(boolean z, boolean z2) {
        mVerifiedLicense = z2;
        mReceivedLicenseResponse = z;
    }

    private class MinecraftLicenseCheckerCallback implements LicenseCheckerCallback {
        private MinecraftLicenseCheckerCallback() {
        }

        @Override
        public void allow(int i) {
//            String str;
//            if (i == 291) {
//                str = new String(StringFog.decrypt("mV6QO1o=\n", "yxvEaQMR9vc=\n"));
//            } else if (i == 256) {
//                str = new String(StringFog.decrypt("NFzvmnTqlVs=\n", "eBWs3zq50B8=\n"));
//            } else {
//                str = new String(StringFog.decrypt("WcLIU4+Hv2teycJOj54=\n", "DIyDHcDQ8Us=\n"));
//            }
            GooglePlayStore.this.updateLicenseStatus(true, true);
//            Log.i(StringFog.decrypt("8Nvqx62dMjbJ/u3Bq4EgNf7a4cGliiET3N7owK+MOA==\n", "vbKEos7vU1A=\n"), String.format(StringFog.decrypt("FQotYQPSlq8GAyB9G9nIr1EV\n", "dGZBDnS38o8=\n"), str));
        }

        @Override
        public void dontAllow(int i) {
//            String str;
//            if (i == 561) {
//                str = new String(StringFog.decrypt("6Yi8PL7ZDHXplK1Y\n", "p8foHPKQTzA=\n"));
//            } else if (i == 291) {
//                str = new String(StringFog.decrypt("qq6Dhrg=\n", "+OvX1OHlBTg=\n"));
//            } else {
//                str = new String(StringFog.decrypt("5UCJfWo56LriS4NgaiA=\n", "sA7CMyVuppo=\n"));
//            }
            GooglePlayStore.this.updateLicenseStatus(true, false);
//            Log.i(StringFog.decrypt("WRq2XaPeUbhgP7FbpcJDu1cbvVuryUKddR+0WqHPWw==\n", "FHPYOMCsMN4=\n"), String.format(StringFog.decrypt("Ao3Kx/9vOjkDidfB9DE6bhU=\n", "ZuikrpoLGks=\n"), str));
        }

        @Override
        public void applicationError(int i) {
            GooglePlayStore.this.updateLicenseStatus(true, false);
//            Log.i(StringFog.decrypt("DeBiVo8tQ7c0xWVQiTFRtAPhaVCHOlCSIeVgUY08SQ==\n", "QIkMM+xfItE=\n"), String.format(StringFog.decrypt("iFUEkkhiqduJ\n", "7Sd2/TpYif4=\n"), Integer.valueOf(i)));
        }
    }

    public GooglePlayStore(MainActivity mainActivity, String str, StoreListener storeListener) {
        this.mListener = storeListener;
        this.mActivity = mainActivity;
        mainActivity.addListener(this);
        this.mBillingImpl = new GooglePlayBillingImpl(this.mActivity, this.mListener, str);
        this.mPolicy = new ServerManagedPolicy(mainActivity, new AESObfuscator(SALT, this.mActivity.getPackageName(), Settings.Secure.
                getString(this.mActivity.getContentResolver(), "android_id")));
        this.mLicenseCheckerCallback = new MinecraftLicenseCheckerCallback();
        LicenseChecker licenseChecker = new LicenseChecker(mainActivity, this.mPolicy, str);
        this.mChecker = licenseChecker;
        licenseChecker.checkAccess(this.mLicenseCheckerCallback);
        if (this.mActivity.isEduMode()) {
            mReceivedLicenseResponse = true;
            mVerifiedLicense = true;
        }
    }

    @Override
    public boolean hasVerifiedLicense() {
        return mVerifiedLicense;
    }

    @Override
    public boolean receivedLicenseResponse() {
        return hasReceivedLicenseResponse();
    }

    @Override
    public ExtraLicenseResponseData getExtraLicenseData() {
        long[] extraLicenseData = this.mPolicy.getExtraLicenseData();
        return new ExtraLicenseResponseData(extraLicenseData[0], extraLicenseData[1], extraLicenseData[2]);
    }

    @Override
    public void queryProducts(final String[] strArr) {
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GooglePlayStore.this.mBillingImpl.queryProducts(strArr);
            }
        });
    }

    @Override
    public void purchase(final String str, final boolean z, final String str2) {
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("TAGG", "Purchase: " + str + " vs " +z);
                if (z) {
                    GooglePlayStore.this.mBillingImpl.launchSubscriptionPurchaseFlow(GooglePlayStore.this.mActivity, str, str2);
                } else {
                    GooglePlayStore.this.mBillingImpl.launchInAppPurchaseFlow(GooglePlayStore.this.mActivity, str, str2);
                }
            }
        });
    }

    @Override
    public void purchaseGame() {
        MainActivity mainActivity = this.mActivity;
        String decrypt = "android.intent.action.VIEW";
        mainActivity.startActivity(new Intent(decrypt, Uri.parse("https://market.android.com/details?id=" + this.mActivity.getPackageName())));
    }

    @Override
    public void acknowledgePurchase(final String str, String str2) {
        this.mActivity.runOnUiThread(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                GooglePlayStore.this.mBillingImpl.consumeOrAckPurchase(str);
            }
        });
    }

    @Override
    public void queryPurchases() {
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GooglePlayStore.this.mBillingImpl.queryPurchases();
            }
        });
    }

    @Override
    public void onResume() {
        queryPurchases();
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
//        Log.v("TAGG", StringFog.decrypt("afghfdqMUhty7zJ73ZBIBib+CWo=\n", "BpZgHq7lJHI=\n"));
    }

    @Override
    public void onDestroy() {
        this.mActivity.removeListener(this);
    }

    @Override // com.mojang.minecraftpe.store.Store
    public void destructor() {
        onDestroy();
    }
}

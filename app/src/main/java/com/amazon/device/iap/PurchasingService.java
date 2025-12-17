package com.amazon.device.iap;

import android.content.Context;
import android.util.Log;
//import com.amazon.device.iap.internal.d;
//import com.amazon.device.iap.internal.e;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.RequestId;
import java.util.Set;

/* loaded from: classes.dex */
public final class PurchasingService {
    public static final String SDK_VERSION = "2.0.61.0";
    private static final String TAG = PurchasingService.class.getSimpleName();
    public static final boolean IS_SANDBOX_MODE = false;

    private PurchasingService() {
        Log.i(TAG, "In-App Purchasing SDK initializing. SDK Version 2.0.61.0, IS_SANDBOX_MODE: " + IS_SANDBOX_MODE);
    }

    public static void registerListener(Context context, PurchasingListener purchasingListener) {
//        d.d().a(context, purchasingListener);
    }

    public static RequestId getUserData() {
        return null;
    }

    public static RequestId purchase(String str) {
        return null;
    }

    public static RequestId getProductData(Set<String> set) {
        return null;
    }

    public static RequestId getPurchaseUpdates(boolean z) {
        return null;
    }

    public static void notifyFulfillment(String str, FulfillmentResult fulfillmentResult) {
//        d.d().a(str, fulfillmentResult);
    }
}
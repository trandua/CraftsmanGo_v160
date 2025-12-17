package com.googleplay.iab;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.vending.billing.IInAppBillingService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes2.dex */
public class IabHelper {
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
    public static final String GET_SKU_DETAILS_ITEM_TYPE_LIST = "ITEM_TYPE_LIST";
    public static final int IABHELPER_BAD_RESPONSE = -1002;
    public static final int IABHELPER_ERROR_BASE = -1000;
    public static final int IABHELPER_INVALID_CONSUMPTION = -1010;
    public static final int IABHELPER_INVALID_SERVICE = -1012;
    public static final int IABHELPER_MISSING_TOKEN = -1007;
    public static final int IABHELPER_REMOTE_EXCEPTION = -1001;
    public static final int IABHELPER_SEND_INTENT_FAILED = -1004;
    public static final int IABHELPER_SUBSCRIPTIONS_NOT_AVAILABLE = -1009;
    public static final int IABHELPER_SUBSCRIPTION_UPDATE_NOT_AVAILABLE = -1011;
    public static final int IABHELPER_UNKNOWN_ERROR = -1008;
    public static final int IABHELPER_UNKNOWN_PURCHASE_RESPONSE = -1006;
    public static final int IABHELPER_USER_CANCELLED = -1005;
    public static final int IABHELPER_VERIFICATION_FAILED = -1003;
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    Context mContext;
    OnIabPurchaseFinishedListener mPurchaseListener;
    String mPurchasingItemType;
    int mRequestCode;
    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    String mSignatureBase64;
    boolean mDebugLog = false;
    String mDebugTag = "IabHelper";
    boolean mSetupDone = false;
    boolean mDisposed = false;
    boolean mDisposeAfterAsync = false;
    boolean mSubscriptionsSupported = false;
    boolean mSubscriptionUpdateSupported = false;
    boolean mAsyncInProgress = false;
    private final Object mAsyncInProgressLock = new Object();
    String mAsyncOperation = "";
    boolean mIsServiceBound = false;

    /* loaded from: classes2.dex */
    public static class IabAsyncInProgressException extends Exception {
        public IabAsyncInProgressException(String str) {
            super(str);
        }
    }

    /* loaded from: classes2.dex */
    public interface OnConsumeFinishedListener {
        void onConsumeFinished(Purchase purchase, IabResult iabResult);
    }

    /* loaded from: classes2.dex */
    public interface OnConsumeMultiFinishedListener {
        void onConsumeMultiFinished(List<Purchase> list, List<IabResult> list2);
    }

    /* loaded from: classes2.dex */
    public interface OnIabPurchaseFinishedListener {
        void onIabPurchaseFinished(IabResult iabResult, Purchase purchase);
    }

    /* loaded from: classes2.dex */
    public interface OnIabSetupFinishedListener {
        void onIabSetupFinished(IabResult iabResult);
    }

    /* loaded from: classes2.dex */
    public interface QueryInventoryFinishedListener {
        void onQueryInventoryFinished(IabResult iabResult, Inventory inventory);
    }

    public IabHelper(Context context, String str) {
        this.mSignatureBase64 = null;
        this.mContext = context.getApplicationContext();
        this.mSignatureBase64 = str;
        logDebug("IAB helper created.");
    }

    private void checkNotDisposed() {
        if (this.mDisposed) {
            throw new IllegalStateException("IabHelper was disposed of, so it cannot be used.");
        }
    }

    public static String getResponseDesc(int i) {
        StringBuilder sb;
        String str;
        String[] split = "0:OK/1:User Canceled/2:Unknown/3:Billing Unavailable/4:Item unavailable/5:Developer Error/6:Error/7:Item Already Owned/8:Item not owned".split("/");
        String[] split2 = "0:OK/-1001:Remote exception during initialization/-1002:Bad response received/-1003:Purchase signature verification failed/-1004:Send intent failed/-1005:User cancelled/-1006:Unknown purchase response/-1007:Missing token/-1008:Unknown error/-1009:Subscriptions not available/-1010:Invalid consumption attempt".split("/");
        if (i <= -1000) {
            int i2 = (-1000) - i;
            if (i2 >= 0 && i2 < split2.length) {
                return split2[i2];
            }
            sb = new StringBuilder();
            sb.append(String.valueOf(i));
            str = ":Unknown IAB Helper Error";
        } else if (i >= 0 && i < split.length) {
            return split[i];
        } else {
            sb = new StringBuilder();
            sb.append(String.valueOf(i));
            str = ":Unknown";
        }
        sb.append(str);
        return sb.toString();
    }

    void checkSetupDone(String str) {
        if (this.mSetupDone) {
            return;
        }
        logError("Illegal state for operation (" + str + "): IAB helper is not set up.");
        throw new IllegalStateException("IAB helper is not set up. Can't perform operation: " + str);
    }

    void consume(Purchase purchase) throws IabException {
        checkNotDisposed();
        checkSetupDone("consume");
        if (!purchase.mItemType.equals("inapp")) {
            throw new IabException((int) IABHELPER_INVALID_CONSUMPTION, "Items of type '" + purchase.mItemType + "' can't be consumed.");
        }
        try {
            String token = purchase.getToken();
            String sku = purchase.getSku();
            if (token == null || token.equals("")) {
                logError("Can't consume " + sku + ". No token.");
                throw new IabException((int) IABHELPER_MISSING_TOKEN, "PurchaseInfo is missing token for sku: " + sku + " " + purchase);
            }
            logDebug("Consuming sku: " + sku + ", token: " + token);
            int consumePurchase = this.mService.consumePurchase(3, this.mContext.getPackageName(), token);
            if (consumePurchase == 0) {
                logDebug("Successfully consumed sku: " + sku);
                return;
            }
            logDebug("Error consuming consuming sku " + sku + ". " + getResponseDesc(consumePurchase));
            throw new IabException(consumePurchase, "Error consuming sku " + sku);
        } catch (RemoteException e) {
            throw new IabException(-1001, "Remote exception while consuming. PurchaseInfo: " + purchase, e);
        }
    }

    public void consumeAsync(Purchase purchase, OnConsumeFinishedListener onConsumeFinishedListener) throws IabAsyncInProgressException {
        checkNotDisposed();
        checkSetupDone("consume");
        ArrayList arrayList = new ArrayList();
        arrayList.add(purchase);
        consumeAsyncInternal(arrayList, onConsumeFinishedListener, null);
    }

    public void consumeAsync(List<Purchase> list, OnConsumeMultiFinishedListener onConsumeMultiFinishedListener) throws IabAsyncInProgressException {
        checkNotDisposed();
        checkSetupDone("consume");
        consumeAsyncInternal(list, null, onConsumeMultiFinishedListener);
    }

    void consumeAsyncInternal(final List<Purchase> list, final OnConsumeFinishedListener onConsumeFinishedListener, final OnConsumeMultiFinishedListener onConsumeMultiFinishedListener) throws IabAsyncInProgressException {
        final Handler handler = new Handler();
        flagStartAsync("consume");
        new Thread(new Runnable() { // from class: com.googleplay.iab.IabHelper.3
            @Override // java.lang.Runnable
            public void run() {
                final ArrayList arrayList = new ArrayList();
                for (Purchase purchase : list) {
                    try {
                        IabHelper.this.consume(purchase);
                        arrayList.add(new IabResult(0, "Successful consume of sku " + purchase.getSku()));
                    } catch (IabException e) {
                        arrayList.add(e.getResult());
                    }
                }
                IabHelper.this.flagEndAsync();
                if (!IabHelper.this.mDisposed && onConsumeFinishedListener != null) {
                    handler.post(new Runnable() { // from class: com.googleplay.iab.IabHelper.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            onConsumeFinishedListener.onConsumeFinished((Purchase) list.get(0), (IabResult) arrayList.get(0));
                        }
                    });
                }
                if (IabHelper.this.mDisposed || onConsumeMultiFinishedListener == null) {
                    return;
                }
                handler.post(new Runnable() { // from class: com.googleplay.iab.IabHelper.3.2
                    @Override // java.lang.Runnable
                    public void run() {
                        onConsumeMultiFinishedListener.onConsumeMultiFinished(list, arrayList);
                    }
                });
            }
        }).start();
    }

    public void dispose() throws IabAsyncInProgressException {
        Context context;
        synchronized (this.mAsyncInProgressLock) {
            if (this.mAsyncInProgress) {
                throw new IabAsyncInProgressException("Can't dispose because an async operation (" + this.mAsyncOperation + ") is in progres.");
            }
        }
        logDebug("Disposing.");
        this.mSetupDone = false;
        if (this.mServiceConn != null) {
            logDebug("Unbinding from service.");
            if (this.mIsServiceBound && (context = this.mContext) != null) {
                context.unbindService(this.mServiceConn);
                this.mIsServiceBound = false;
            }
        }
        this.mDisposed = true;
        this.mContext = null;
        this.mServiceConn = null;
        this.mService = null;
        this.mPurchaseListener = null;
    }

    public void disposeWhenFinished() {
        synchronized (this.mAsyncInProgressLock) {
            if (this.mAsyncInProgress) {
                logDebug("Will dispose after async operation finishes.");
                this.mDisposeAfterAsync = true;
            } else {
                try {
                    dispose();
                } catch (IabAsyncInProgressException unused) {
                }
            }
        }
    }

    public void enableDebugLogging(boolean z) {
        checkNotDisposed();
        this.mDebugLog = z;
    }

    public void enableDebugLogging(boolean z, String str) {
        checkNotDisposed();
        this.mDebugLog = z;
        this.mDebugTag = str;
    }

    void flagEndAsync() {
        synchronized (this.mAsyncInProgressLock) {
            logDebug("Ending async operation: " + this.mAsyncOperation);
            this.mAsyncOperation = "";
            this.mAsyncInProgress = false;
            if (this.mDisposeAfterAsync) {
                try {
                    dispose();
                } catch (IabAsyncInProgressException unused) {
                }
            }
        }
    }

    void flagStartAsync(String str) throws IabAsyncInProgressException {
        synchronized (this.mAsyncInProgressLock) {
            if (this.mAsyncInProgress) {
                throw new IabAsyncInProgressException("Can't start async operation (" + str + ") because another async operation (" + this.mAsyncOperation + ") is in progress.");
            }
            this.mAsyncOperation = str;
            this.mAsyncInProgress = true;
            logDebug("Starting async operation: " + str);
        }
    }

    int getResponseCodeFromBundle(Bundle bundle) {
        Object obj = bundle.get(RESPONSE_CODE);
        if (obj == null) {
            logDebug("Bundle with null response code, assuming OK (known issue)");
            return 0;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else {
            if (obj instanceof Long) {
                return (int) ((Long) obj).longValue();
            }
            logError("Unexpected type for bundle response code.");
            logError(obj.getClass().getName());
            throw new RuntimeException("Unexpected type for bundle response code: " + obj.getClass().getName());
        }
    }

    int getResponseCodeFromIntent(Intent intent) {
        Object obj = intent.getExtras().get(RESPONSE_CODE);
        if (obj == null) {
            logError("Intent with no response code, assuming OK (known issue)");
            return 0;
        } else if (obj instanceof Integer) {
            return ((Integer) obj).intValue();
        } else {
            if (obj instanceof Long) {
                return (int) ((Long) obj).longValue();
            }
            logError("Unexpected type for intent response code.");
            logError(obj.getClass().getName());
            throw new RuntimeException("Unexpected type for intent response code: " + obj.getClass().getName());
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:45:0x0175, code lost:
        if (r11 != null) goto L43;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x01a2, code lost:
        if (r11 != null) goto L43;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean handleActivityResult(int i, int i2, Intent intent) {
        IabResult iabResult;
        OnIabPurchaseFinishedListener onIabPurchaseFinishedListener;
        if (i != this.mRequestCode) {
            return false;
        }
        checkNotDisposed();
        checkSetupDone("handleActivityResult");
        flagEndAsync();
        if (intent == null) {
            logError("Null data in IAB activity result.");
            IabResult iabResult2 = new IabResult(IABHELPER_BAD_RESPONSE, "Null data in IAB result");
            OnIabPurchaseFinishedListener onIabPurchaseFinishedListener2 = this.mPurchaseListener;
            if (onIabPurchaseFinishedListener2 != null) {
                onIabPurchaseFinishedListener2.onIabPurchaseFinished(iabResult2, null);
            }
            return true;
        }
        int responseCodeFromIntent = getResponseCodeFromIntent(intent);
        String stringExtra = intent.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
        String stringExtra2 = intent.getStringExtra(RESPONSE_INAPP_SIGNATURE);
        if (i2 == -1 && responseCodeFromIntent == 0) {
            logDebug("Successful resultcode from purchase activity.");
            logDebug("Purchase data: " + stringExtra);
            logDebug("Data signature: " + stringExtra2);
            logDebug("Extras: " + intent.getExtras());
            logDebug("Expected item type: " + this.mPurchasingItemType);
            if (stringExtra == null || stringExtra2 == null) {
                logError("BUG: either purchaseData or dataSignature is null.");
                logDebug("Extras: " + intent.getExtras().toString());
                IabResult iabResult3 = new IabResult(IABHELPER_UNKNOWN_ERROR, "IAB returned null purchaseData or dataSignature");
                OnIabPurchaseFinishedListener onIabPurchaseFinishedListener3 = this.mPurchaseListener;
                if (onIabPurchaseFinishedListener3 != null) {
                    onIabPurchaseFinishedListener3.onIabPurchaseFinished(iabResult3, null);
                }
                return true;
            }
            try {
                Purchase purchase = new Purchase(this.mPurchasingItemType, stringExtra, stringExtra2);
                String sku = purchase.getSku();
                if (!Security.verifyPurchase(this.mSignatureBase64, stringExtra, stringExtra2)) {
                    logError("Purchase signature verification FAILED for sku " + sku);
                    IabResult iabResult4 = new IabResult(IABHELPER_VERIFICATION_FAILED, "Signature verification failed for sku " + sku);
                    OnIabPurchaseFinishedListener onIabPurchaseFinishedListener4 = this.mPurchaseListener;
                    if (onIabPurchaseFinishedListener4 != null) {
                        onIabPurchaseFinishedListener4.onIabPurchaseFinished(iabResult4, purchase);
                    }
                    return true;
                }
                logDebug("Purchase signature successfully verified.");
                OnIabPurchaseFinishedListener onIabPurchaseFinishedListener5 = this.mPurchaseListener;
                if (onIabPurchaseFinishedListener5 != null) {
                    onIabPurchaseFinishedListener5.onIabPurchaseFinished(new IabResult(0, "Success"), purchase);
                }
            } catch (JSONException e) {
                logError("Failed to parse purchase data.");
                e.printStackTrace();
                IabResult iabResult5 = new IabResult(IABHELPER_BAD_RESPONSE, "Failed to parse purchase data.");
                OnIabPurchaseFinishedListener onIabPurchaseFinishedListener6 = this.mPurchaseListener;
                if (onIabPurchaseFinishedListener6 != null) {
                    onIabPurchaseFinishedListener6.onIabPurchaseFinished(iabResult5, null);
                }
                return true;
            }
        } else if (i2 == -1) {
            logDebug("Result code was OK but in-app billing response was not OK: " + getResponseDesc(responseCodeFromIntent));
            if (this.mPurchaseListener != null) {
                iabResult = new IabResult(responseCodeFromIntent, "Problem purchashing item.");
                onIabPurchaseFinishedListener = this.mPurchaseListener;
                onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult, null);
            }
        } else if (i2 == 0) {
            logDebug("Purchase canceled - Response: " + getResponseDesc(responseCodeFromIntent));
            iabResult = new IabResult(IABHELPER_USER_CANCELLED, "User canceled.");
            onIabPurchaseFinishedListener = this.mPurchaseListener;
        } else {
            logError("Purchase failed. Result code: " + Integer.toString(i2) + ". Response: " + getResponseDesc(responseCodeFromIntent));
            iabResult = new IabResult(IABHELPER_UNKNOWN_PURCHASE_RESPONSE, "Unknown purchase response.");
            onIabPurchaseFinishedListener = this.mPurchaseListener;
        }
        return true;
    }

    public void launchPurchaseFlow(Activity activity, String str, int i, OnIabPurchaseFinishedListener onIabPurchaseFinishedListener) throws IabAsyncInProgressException {
        launchPurchaseFlow(activity, str, i, onIabPurchaseFinishedListener, "");
    }

    public void launchPurchaseFlow(Activity activity, String str, int i, OnIabPurchaseFinishedListener onIabPurchaseFinishedListener, String str2) throws IabAsyncInProgressException {
        launchPurchaseFlow(activity, str, "inapp", null, i, onIabPurchaseFinishedListener, str2);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x009e A[Catch: RemoteException -> 0x0110, SendIntentException -> 0x0134, TryCatch #2 {SendIntentException -> 0x0134, RemoteException -> 0x0110, blocks: (B:10:0x0039, B:12:0x0052, B:15:0x0059, B:17:0x005d, B:19:0x006b, B:21:0x006f, B:23:0x0098, B:25:0x009e, B:27:0x00bd, B:29:0x00c1, B:22:0x0085), top: B:40:0x0039 }] */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00c1 A[Catch: RemoteException -> 0x0110, SendIntentException -> 0x0134, TRY_LEAVE, TryCatch #2 {SendIntentException -> 0x0134, RemoteException -> 0x0110, blocks: (B:10:0x0039, B:12:0x0052, B:15:0x0059, B:17:0x005d, B:19:0x006b, B:21:0x006f, B:23:0x0098, B:25:0x009e, B:27:0x00bd, B:29:0x00c1, B:22:0x0085), top: B:40:0x0039 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void launchPurchaseFlow(Activity activity, String str, String str2, List<String> list, int i, OnIabPurchaseFinishedListener onIabPurchaseFinishedListener, String str3) throws IabAsyncInProgressException {
        IabResult iabResult;
        Bundle buyIntent;
        int responseCodeFromBundle;
        checkNotDisposed();
        checkSetupDone("launchPurchaseFlow");
        flagStartAsync("launchPurchaseFlow");
        if (str2.equals("subs") && !this.mSubscriptionsSupported) {
            IabResult iabResult2 = new IabResult(-1009, "Subscriptions are not available.");
            flagEndAsync();
            if (onIabPurchaseFinishedListener != null) {
                onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult2, null);
                return;
            }
            return;
        }
        try {
            logDebug("Constructing buy intent for " + str + ", item type: " + str2);
            if (list != null && !list.isEmpty()) {
                if (!this.mSubscriptionUpdateSupported) {
                    IabResult iabResult3 = new IabResult(IABHELPER_SUBSCRIPTION_UPDATE_NOT_AVAILABLE, "Subscription updates are not available.");
                    flagEndAsync();
                    if (onIabPurchaseFinishedListener != null) {
                        onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult3, null);
                        return;
                    }
                    return;
                }
                buyIntent = this.mService.getBuyIntentToReplaceSkus(5, this.mContext.getPackageName(), list, str, str2, str3);
                responseCodeFromBundle = getResponseCodeFromBundle(buyIntent);
                if (responseCodeFromBundle == 0) {
                    logError("Unable to buy item, Error response: " + getResponseDesc(responseCodeFromBundle));
                    flagEndAsync();
                    IabResult iabResult4 = new IabResult(responseCodeFromBundle, "Unable to buy item");
                    if (onIabPurchaseFinishedListener != null) {
                        onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult4, null);
                        return;
                    }
                    return;
                }
                logDebug("Launching buy intent for " + str + ". Request code: " + i);
                this.mRequestCode = i;
                this.mPurchaseListener = onIabPurchaseFinishedListener;
                this.mPurchasingItemType = str2;
                IntentSender intentSender = ((PendingIntent) buyIntent.getParcelable(RESPONSE_BUY_INTENT)).getIntentSender();
                Intent intent = new Intent();
                Integer num = 0;
                int intValue = num.intValue();
                Integer num2 = 0;
                Integer num3 = 0;
                activity.startIntentSenderForResult(intentSender, i, intent, intValue, num2.intValue(), num3.intValue());
                return;
            }
            buyIntent = this.mService.getBuyIntent(3, this.mContext.getPackageName(), str, str2, str3);
            responseCodeFromBundle = getResponseCodeFromBundle(buyIntent);
            if (responseCodeFromBundle == 0) {
            }
        } catch (IntentSender.SendIntentException e) {
            logError("SendIntentException while launching purchase flow for sku " + str);
            e.printStackTrace();
            flagEndAsync();
            iabResult = new IabResult(IABHELPER_SEND_INTENT_FAILED, "Failed to send intent.");
            if (onIabPurchaseFinishedListener == null) {
                return;
            }
            onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult, null);
        } catch (RemoteException e2) {
            logError("RemoteException while launching purchase flow for sku " + str);
            e2.printStackTrace();
            flagEndAsync();
            iabResult = new IabResult(-1001, "Remote exception while starting purchase flow");
            if (onIabPurchaseFinishedListener == null) {
                return;
            }
            onIabPurchaseFinishedListener.onIabPurchaseFinished(iabResult, null);
        }
    }

    public void launchSubscriptionPurchaseFlow(Activity activity, String str, int i, OnIabPurchaseFinishedListener onIabPurchaseFinishedListener) throws IabAsyncInProgressException {
        launchSubscriptionPurchaseFlow(activity, str, i, onIabPurchaseFinishedListener, "");
    }

    public void launchSubscriptionPurchaseFlow(Activity activity, String str, int i, OnIabPurchaseFinishedListener onIabPurchaseFinishedListener, String str2) throws IabAsyncInProgressException {
        launchPurchaseFlow(activity, str, "subs", null, i, onIabPurchaseFinishedListener, str2);
    }

    void logDebug(String str) {
        if (this.mDebugLog) {
            Log.d(this.mDebugTag, str);
        }
    }

    void logError(String str) {
        String str2 = this.mDebugTag;
        Log.e(str2, "In-app billing error: " + str);
    }

    void logWarn(String str) {
        String str2 = this.mDebugTag;
        Log.w(str2, "In-app billing warning: " + str);
    }

    public Inventory queryInventory() throws IabException {
        return queryInventory(false, null, null);
    }

    public Inventory queryInventory(boolean z, List<String> list, List<String> list2) throws IabException {
        int querySkuDetails;
        int querySkuDetails2;
        checkNotDisposed();
        checkSetupDone("queryInventory");
        try {
            Inventory inventory = new Inventory();
            int queryPurchases = queryPurchases(inventory, "inapp");
            if (queryPurchases == 0) {
                if (z && (querySkuDetails2 = querySkuDetails("inapp", inventory, list2)) != 0) {
                    throw new IabException(querySkuDetails2, "Error refreshing inventory (querying prices of items).");
                }
                if (this.mSubscriptionsSupported) {
                    int queryPurchases2 = queryPurchases(inventory, "subs");
                    if (queryPurchases2 != 0) {
                        throw new IabException(queryPurchases2, "Error refreshing inventory (querying owned subscriptions).");
                    }
                    if (z && (querySkuDetails = querySkuDetails("subs", inventory, list)) != 0) {
                        throw new IabException(querySkuDetails, "Error refreshing inventory (querying prices of subscriptions).");
                    }
                }
                return inventory;
            }
            throw new IabException(queryPurchases, "Error refreshing inventory (querying owned items).");
        } catch (RemoteException e) {
            throw new IabException(-1001, "Remote exception while refreshing inventory.", e);
        } catch (JSONException e2) {
            throw new IabException(IABHELPER_BAD_RESPONSE, "Error parsing JSON response while refreshing inventory.", e2);
        }
    }

    public void queryInventoryAsync(QueryInventoryFinishedListener queryInventoryFinishedListener) throws IabAsyncInProgressException {
        queryInventoryAsync(false, null, null, queryInventoryFinishedListener);
    }

    public void queryInventoryAsync(final boolean querySkuDetails, final List<String> moreItemSkus,
                                    final List<String> moreSubsSkus, final QueryInventoryFinishedListener listener) throws IabAsyncInProgressException {
//        final Handler handler = new Handler();
//        checkNotDisposed();
//        checkSetupDone("queryInventory");
//        flagStartAsync("refresh inventory");
//        new Thread(new Runnable() { // from class: com.googleplay.iab.IabHelper.2
//            @Override // java.lang.Runnable
//            public void run() {
//                Inventory inventory = null;
//                IabResult iabResult = new IabResult(0, "Inventory refresh successful.");
//                try {
//                    inventory = IabHelper.this.queryInventory(z, list, list2);
//                } catch (IabException e) {
//                    iabResult = e.getResult();
////                    inventory = null;
//                    e.printStackTrace();
//                }
//                IabHelper.this.flagEndAsync();
//                if (IabHelper.this.mDisposed || queryInventoryFinishedListener == null) {
//                    return;
//                }
//                handler.post(new Runnable() { // from class: com.googleplay.iab.IabHelper.2.1
//                    @Override // java.lang.Runnable
//                    public void run() {
//                        queryInventoryFinishedListener.onQueryInventoryFinished(iabResult, inventory);
//                    }
//                });
//            }
//        }).start();
        final Handler handler = new Handler();
        checkNotDisposed();
        checkSetupDone("queryInventory");
//        checkServiceConnected("queryInventory");
        flagStartAsync("refresh inventory");
        (new Thread(new Runnable() {
            public void run() {
                IabResult result = new IabResult(BILLING_RESPONSE_RESULT_OK, "Inventory refresh successful.");
                Inventory inv = null;
                try {
                    inv = queryInventory(querySkuDetails, moreItemSkus, moreSubsSkus);
                } catch (IabException ex) {
                    result = ex.getResult();
                }

                flagEndAsync();

                final IabResult result_f = result;
                final Inventory inv_f = inv;
                if (!mDisposed && listener != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            listener.onQueryInventoryFinished(result_f, inv_f);
                        }
                    });
                }
            }
        })).start();
    }

    int queryPurchases(Inventory inventory, String str) throws JSONException, RemoteException {
        logDebug("Querying owned items, item type: " + str);
        logDebug("Package name: " + this.mContext.getPackageName());
        if (this.mService == null) {
            return IABHELPER_INVALID_SERVICE;
        }
        String str2 = null;
        boolean z = false;
        do {
            logDebug("Calling getPurchases with continuation token: " + str2);
            Bundle purchases = this.mService.getPurchases(3, this.mContext.getPackageName(), str, str2);
            int responseCodeFromBundle = getResponseCodeFromBundle(purchases);
            logDebug("Owned items response: " + String.valueOf(responseCodeFromBundle));
            if (responseCodeFromBundle != 0) {
                logDebug("getPurchases() failed: " + getResponseDesc(responseCodeFromBundle));
                return responseCodeFromBundle;
            } else if (!purchases.containsKey(RESPONSE_INAPP_ITEM_LIST) || !purchases.containsKey(RESPONSE_INAPP_PURCHASE_DATA_LIST) || !purchases.containsKey(RESPONSE_INAPP_SIGNATURE_LIST)) {
                logError("Bundle returned from getPurchases() doesn't contain required fields.");
                return IABHELPER_BAD_RESPONSE;
            } else {
                ArrayList<String> stringArrayList = purchases.getStringArrayList(RESPONSE_INAPP_ITEM_LIST);
                ArrayList<String> stringArrayList2 = purchases.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST);
                ArrayList<String> stringArrayList3 = purchases.getStringArrayList(RESPONSE_INAPP_SIGNATURE_LIST);
                for (int i = 0; i < stringArrayList2.size(); i++) {
                    String str3 = stringArrayList2.get(i);
                    String str4 = stringArrayList3.get(i);
                    String str5 = stringArrayList.get(i);
                    if (Security.verifyPurchase(this.mSignatureBase64, str3, str4)) {
                        logDebug("Sku is owned: " + str5);
                        Purchase purchase = new Purchase(str, str3, str4);
                        if (TextUtils.isEmpty(purchase.getToken())) {
                            logWarn("BUG: empty/null token!");
                            logDebug("Purchase data: " + str3);
                        }
                        inventory.addPurchase(purchase);
                    } else {
                        logWarn("Purchase signature verification **FAILED**. Not adding item.");
                        logDebug("   Purchase data: " + str3);
                        logDebug("   Signature: " + str4);
                        z = true;
                    }
                }
                str2 = purchases.getString(INAPP_CONTINUATION_TOKEN);
                logDebug("Continuation token: " + str2);
            }
        } while (!TextUtils.isEmpty(str2));
        if (z) {
            return IABHELPER_VERIFICATION_FAILED;
        }
        return 0;
    }

    int querySkuDetails(String itemType, Inventory inv, List<String> moreSkus) throws RemoteException, JSONException {
//        logDebug("Querying SKU details.");
//        if (this.mService == null || this.mContext == null) {
//            return IABHELPER_INVALID_SERVICE;
//        }
//        ArrayList arrayList = new ArrayList();
//        arrayList.addAll(inventory.getAllOwnedSkus(str));
//        if (list != null) {
//            for (String str2 : list) {
//                if (!arrayList.contains(str2)) {
//                    arrayList.add(str2);
//                }
//            }
//        }
//        if (arrayList.size() == 0) {
//            logDebug("queryPrices: nothing to do because there are no SKUs.");
//            return 0;
//        }
//        ArrayList arrayList2 = new ArrayList();
//        int size = arrayList.size() / 20;
//        int size2 = arrayList.size() % 20;
//        for (int i = 0; i < size; i++) {
//            ArrayList arrayList3 = new ArrayList();
//            int i2 = i * 20;
//            for (String str3 : arrayList.subList(i2, i2 + 20)) {
//                arrayList3.add(str3);
//            }
//            arrayList2.add(arrayList3);
//        }
//        if (size2 != 0) {
//            ArrayList arrayList4 = new ArrayList();
//            int i3 = size * 20;
//            for (String str4 : arrayList.subList(i3, size2 + i3)) {
//                arrayList4.add(str4);
//            }
//            arrayList2.add(arrayList4);
//        }
//
//        Iterator it = arrayList2.iterator();
//        while (it.hasNext()) {
//            Bundle bundle = new Bundle();
//            bundle.putStringArrayList(GET_SKU_DETAILS_ITEM_LIST, (ArrayList) it.next());
//            Bundle skuDetails = this.mService.getSkuDetails(3, this.mContext.getPackageName(), str, bundle);
//            if (!skuDetails.containsKey(RESPONSE_GET_SKU_DETAILS_LIST)) {
//                int responseCodeFromBundle = getResponseCodeFromBundle(skuDetails);
//                if (responseCodeFromBundle == 0) {
//                    logError("getSkuDetails() returned a bundle with neither an error nor a detail list.");
//                    return IABHELPER_BAD_RESPONSE;
//                }
//                logDebug("getSkuDetails() failed: " + getResponseDesc(responseCodeFromBundle));
//                return responseCodeFromBundle;
//            }
//            Iterator<String> it2 = skuDetails.getStringArrayList(RESPONSE_GET_SKU_DETAILS_LIST).iterator();
//            while (it2.hasNext()) {
//                SkuDetails skuDetails2 = new SkuDetails(str, it2.next());
//                logDebug("Got sku details: " + skuDetails2);
//                inventory.addSkuDetails(skuDetails2);
//            }
//        }
//        return 0;
        logDebug("Querying SKU details.");
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.addAll(inv.getAllOwnedSkus(itemType));
        if (moreSkus != null) {
            for (String sku : moreSkus) {
                if (!skuList.contains(sku)) {
                    skuList.add(sku);
                }
            }
        }

        if (skuList.size() == 0) {
            logDebug("queryPrices: nothing to do because there are no SKUs.");
            return BILLING_RESPONSE_RESULT_OK;
        }

        // Split the sku list in blocks of no more than 20 elements.
        ArrayList<ArrayList<String>> packs = new ArrayList<ArrayList<String>>();
        ArrayList<String> tempList;
        int n = skuList.size() / 20;
        int mod = skuList.size() % 20;
        for (int i = 0; i < n; i++) {
            tempList = new ArrayList<String>();
            for (String s : skuList.subList(i * 20, i * 20 + 20)) {
                tempList.add(s);
            }
            packs.add(tempList);
        }
        if (mod != 0) {
            tempList = new ArrayList<String>();
            for (String s : skuList.subList(n * 20, n * 20 + mod)) {
                tempList.add(s);
            }
            packs.add(tempList);
        }

        for (ArrayList<String> skuPartList : packs) {
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList(GET_SKU_DETAILS_ITEM_LIST, skuPartList);
            Bundle skuDetails = mService.getSkuDetails(3, mContext.getPackageName(),
                    itemType, querySkus);

            if (!skuDetails.containsKey(RESPONSE_GET_SKU_DETAILS_LIST)) {
                int response = getResponseCodeFromBundle(skuDetails);
                if (response != BILLING_RESPONSE_RESULT_OK) {
                    logDebug("getSkuDetails() failed: " + getResponseDesc(response));
                    return response;
                } else {
                    logError("getSkuDetails() returned a bundle with neither an error nor a detail list.");
                    return IABHELPER_BAD_RESPONSE;
                }
            }

            ArrayList<String> responseList = skuDetails.getStringArrayList(
                    RESPONSE_GET_SKU_DETAILS_LIST);

            for (String thisResponse : responseList) {
                SkuDetails d = new SkuDetails(itemType, thisResponse);
                logDebug("Got sku details: " + d);
                inv.addSkuDetails(d);
            }
        }

        return BILLING_RESPONSE_RESULT_OK;
    }

    public void startSetup(final OnIabSetupFinishedListener onIabSetupFinishedListener) {
        checkNotDisposed();
        if (this.mSetupDone) {
            throw new IllegalStateException("IAB helper is already set up.");
        }
        logDebug("Starting in-app billing setup.");
        this.mServiceConn = new ServiceConnection() { // from class: com.googleplay.iab.IabHelper.1
            /* JADX WARN: Removed duplicated region for block: B:28:0x00c4  */
            /* JADX WARN: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
            @Override // android.content.ServiceConnection
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                IabHelper iabHelper;
                OnIabSetupFinishedListener onIabSetupFinishedListener2;
                if (IabHelper.this.mDisposed) {
                    return;
                }
                IabHelper.this.logDebug("Billing service connected.");
                IabHelper.this.mService = IInAppBillingService.Stub.asInterface(iBinder);
                String packageName = IabHelper.this.mContext.getPackageName();
                try {
                    IabHelper.this.logDebug("Checking for in-app billing 3 support.");
                    int isBillingSupported = IabHelper.this.mService.isBillingSupported(3, packageName, "inapp");
                    if (isBillingSupported != 0) {
                        OnIabSetupFinishedListener onIabSetupFinishedListener3 = onIabSetupFinishedListener;
                        if (onIabSetupFinishedListener3 != null) {
                            onIabSetupFinishedListener3.onIabSetupFinished(new IabResult(isBillingSupported, "Error checking for billing v3 support."));
                        }
                        IabHelper.this.mSubscriptionsSupported = false;
                        IabHelper.this.mSubscriptionUpdateSupported = false;
                        return;
                    }
                    IabHelper iabHelper2 = IabHelper.this;
                    iabHelper2.logDebug("In-app billing version 3 supported for " + packageName);
                    if (IabHelper.this.mService.isBillingSupported(5, packageName, "subs") == 0) {
                        IabHelper.this.logDebug("Subscription re-signup AVAILABLE.");
                        IabHelper.this.mSubscriptionUpdateSupported = true;
                    } else {
                        IabHelper.this.logDebug("Subscription re-signup not available.");
                        IabHelper.this.mSubscriptionUpdateSupported = false;
                    }
                    if (IabHelper.this.mSubscriptionUpdateSupported) {
                        iabHelper = IabHelper.this;
                    } else {
                        int isBillingSupported2 = IabHelper.this.mService.isBillingSupported(3, packageName, "subs");
                        if (isBillingSupported2 != 0) {
                            IabHelper iabHelper3 = IabHelper.this;
                            iabHelper3.logDebug("Subscriptions NOT AVAILABLE. Response: " + isBillingSupported2);
                            IabHelper.this.mSubscriptionsSupported = false;
                            IabHelper.this.mSubscriptionUpdateSupported = false;
                            IabHelper.this.mSetupDone = true;
                            onIabSetupFinishedListener2 = onIabSetupFinishedListener;
                            if (onIabSetupFinishedListener2 == null) {
                                onIabSetupFinishedListener2.onIabSetupFinished(new IabResult(0, "Setup successful."));
                                return;
                            }
                            return;
                        }
                        IabHelper.this.logDebug("Subscriptions AVAILABLE.");
                        iabHelper = IabHelper.this;
                    }
                    iabHelper.mSubscriptionsSupported = true;
                    IabHelper.this.mSetupDone = true;
                    onIabSetupFinishedListener2 = onIabSetupFinishedListener;
                    if (onIabSetupFinishedListener2 == null) {
                    }
                } catch (RemoteException e) {
                    OnIabSetupFinishedListener onIabSetupFinishedListener4 = onIabSetupFinishedListener;
                    if (onIabSetupFinishedListener4 != null) {
                        onIabSetupFinishedListener4.onIabSetupFinished(new IabResult(-1001, "RemoteException while setting up in-app billing."));
                    }
                    e.printStackTrace();
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName componentName) {
                IabHelper.this.logDebug("Billing service disconnected.");
                IabHelper.this.mService = null;
            }
        };
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        List<ResolveInfo> queryIntentServices = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        if (queryIntentServices == null || queryIntentServices.isEmpty()) {
            if (onIabSetupFinishedListener != null) {
                onIabSetupFinishedListener.onIabSetupFinished(new IabResult(3, "Billing service unavailable on device."));
                return;
            }
            return;
        }
        boolean bindService = this.mContext.bindService(intent, this.mServiceConn, 1);
        this.mIsServiceBound = bindService;
        if (bindService) {
            return;
        }
        logDebug("Problem in binding service intent '" + intent.getAction() + "', this is unexpected.");
    }

    public boolean subscriptionsSupported() {
        checkNotDisposed();
        return this.mSubscriptionsSupported;
    }
}

package com.mojang.minecraftpe.store.googleplay;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
//import com.craftsman.go.StringFog;
import com.mojang.minecraftpe.store.Product;
import com.mojang.minecraftpe.store.StoreListener;
import handheld.project.android.src.com.mojang.minecraftpe.store.googleplay.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/* loaded from: classes3.dex */
public class GooglePlayBillingImpl implements PurchasesUpdatedListener, PurchasesResponseListener, SkuDetailsResponseListener, AcknowledgePurchaseResponseListener, ConsumeResponseListener {
    private static String mNewSubscriptionTag = "NEW_SUB";
    private Activity mActivity;
    private BillingClient mBillingClient;
    public StoreListener mListener;
    private String mSignatureBase64;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap();
    private String mSkuInProgress;
    private String mWorldName;

    public GooglePlayBillingImpl(Activity activity, StoreListener storeListener, String str) {
        this.mActivity = activity;
        this.mListener = storeListener;
        this.mSignatureBase64 = str;
        initialize();
    }

    private void initialize() {
        BillingClient build = BillingClient.newBuilder(this.mActivity).setListener(this).enablePendingPurchases().build();
        this.mBillingClient = build;
        build.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                GooglePlayBillingImpl.this.mListener.onStoreInitialized(billingResult.getResponseCode() == 0);
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d("TAGG", "Billing service disconnected.");
            }
        });
    }

    public void launchInAppPurchaseFlow(Activity activity, String str, String str2) {
        Log.v("TAGG", "launchInAppPurchaseFlowwwwwwwwwwwwwwwww");
        if (!this.mBillingClient.isReady()) {
            Log.v("TAGG", "Billing client is not ready when launching purchase flow");
            return;
        }
        Map<String, SkuDetails> map = this.mSkuDetailsMap;
        if (map == null) {
            Log.v("TAGG", "mSkuDetails map is null");
            return;
        }
        SkuDetails skuDetails = map.get(str);
        if (skuDetails == null) {
            Log.v("TAGG", "Unable to find SKU");
            return;
        }
        this.mSkuInProgress = str;
        BillingResult launchBillingFlow = this.mBillingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).setObfuscatedAccountId(Base64.encodeToString(str2.getBytes(), 0)).build());
        String decrypt = "";
        Log.v("TAGG'", " launchInAppBillingFlow: BillingResponse " + launchBillingFlow.getResponseCode() + " " + launchBillingFlow.getDebugMessage());
    }

    public void launchSubscriptionPurchaseFlow(Activity activity, String str, String str2) {
        if (!this.mBillingClient.isReady()) {
            Log.v("TAGG", "Billing client is not ready when launching purchase flow");
            return;
        }
        Map<String, SkuDetails> map = this.mSkuDetailsMap;
        if (map == null) {
            Log.v("TAGG", "mSkuDetails map is null");
            return;
        }
        SkuDetails skuDetails = map.get(str);
        if (skuDetails == null) {
            Log.v("TAGG", "Unable to find SKU");
            return;
        }
        this.mSkuInProgress = str;
        try {
            JSONObject jSONObject = (JSONObject) new JSONTokener(str2).nextValue();
            String string = jSONObject.getString("subscription_id");
            if (string.isEmpty()) {
                string = mNewSubscriptionTag;
            }
            this.mWorldName = jSONObject.getString("world_name");
            BillingResult launchBillingFlow = this.mBillingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).
                    setObfuscatedAccountId(Base64.encodeToString(new JSONObject(jSONObject, new String[]{"xuid"}).toString().getBytes(), 0)).setObfuscatedProfileId(Base64.encodeToString(string.getBytes(), 0)).build());
//            String decrypt = StringFog.decrypt("tVSdbrFVFIOTQrBgsVwtgZVyn3mx\n", "8jvyCd0wRO8=\n");
            Log.v("TAGG", "launchSubscriptionBillingFlow: BillingResponse " + launchBillingFlow.getResponseCode() + " " + launchBillingFlow.getDebugMessage());
        } catch (JSONException e) {
            Log.e("TAGG", e.getLocalizedMessage());
        }
    }

    public void queryPurchases() {
        if (this.mBillingClient.isReady()) {
            this.mBillingClient.queryPurchasesAsync("inapp", this);
            if (this.mBillingClient.isFeatureSupported("subscriptions").getResponseCode() == 0) {
                this.mBillingClient.queryPurchasesAsync("subs", this);
                return;
            }
            return;
        }
        Log.v("TAGG", "Billing client is not ready when querying purchases");
    }

    public void queryProducts(String[] strArr) {
        if (this.mBillingClient.isReady()) {
            this.mBillingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder().setType("inapp").setSkusList(Arrays.asList(strArr)).build(), this);
            if (this.mBillingClient.isFeatureSupported("subscriptions").getResponseCode() == 0) {
                this.mBillingClient.querySkuDetailsAsync(SkuDetailsParams.newBuilder().setType("subs").setSkusList(Arrays.asList(strArr)).build(), this);
                return;
            }
            return;
        }
        Log.v("TAGG", "Billing client is not ready when querying products");
    }

    public void consumeOrAckPurchase(String str) {
        Purchase parseReceipt = parseReceipt(str);
        if (parseReceipt == null) {
            Log.v("TAGG", "consumeOrAckPurchase has null purchase");
        } else if (this.mSkuDetailsMap.get(parseReceipt.getSkus().get(0)).getType().equals("inapp")) {
            consumePurchase(parseReceipt);
        } else if (parseReceipt.isAcknowledged()) {
        } else {
            acknowledgePurchase(parseReceipt.getPurchaseToken());
        }
    }

    public void acknowledgePurchase(String str) {
        if (this.mBillingClient.isReady()) {
            Log.v("TAGG", "Acknowledging purchase");
            this.mBillingClient.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder().setPurchaseToken(str).build(), this);
            return;
        }
        Log.v("TAGG", "Billing client is not ready when acknowledging purchase");
    }

    public void consumePurchase(Purchase purchase) {
        if (!this.mBillingClient.isReady()) {
            Log.v("TAGG", "Billing client is not ready when consuming purchase");
        } else if (purchase.getPurchaseState() == 1) {
            this.mBillingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(), this);
        } else {
            Log.v("TAGG", "Purchase is not in PurchasedState");
        }
    }

    @Override // com.android.billingclient.api.ConsumeResponseListener
    public void onConsumeResponse(BillingResult billingResult, String str) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
        //String decrypt = StringFog.decrypt("hJeZtAotuAqigbS6CiSBCKSxm6MK\n", "w/j202ZI6GY=\n");
        Log.v("TAGG", "onConsumeResponse: BillingResponse " + responseCode + " " + debugMessage);
        if (billingResult.getResponseCode() == 0) {
            Log.v("TAGG", "ConsumeSuccess");
        } else {
            Log.v("TAGG", "ConsumeFail");
        }
    }

    @Override // com.android.billingclient.api.AcknowledgePurchaseResponseListener
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
//        String decrypt = StringFog.decrypt("pJfo1kDZlkyCgcXYQNCvToSx6sFA\n", "4/iHsSy8xiA=\n");
        Log.v("TAGG","onAcknowledgePurchaseResponse: BillingResponse "  + responseCode + " " + debugMessage);
        if (billingResult.getResponseCode() == 0) {
            Log.v("TAGG", "AckSuccess");
        } else {
            Log.v("TAGG", "AckFail");
        }
    }

    @Override // com.android.billingclient.api.PurchasesResponseListener
    public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> list) {
        int responseCode = billingResult.getResponseCode();
        String debugMessage = billingResult.getDebugMessage();
//        String decrypt = StringFog.decrypt("RqQ8y4USPAVgshHFhRsFB2aCPtyF\n", "ActTrOl3bGk=\n");
        Log.v("TAGG", "onQueryPurchasesResponse: BillingResponse " + responseCode + " " + debugMessage);
        if (billingResult.getResponseCode() == 0) {
            ArrayList arrayList = new ArrayList();
            Iterator<Purchase> it = list.iterator();
            while (true) {
                if (it.hasNext()) {
                    Purchase next = it.next();
                    ArrayList<String> skus = next.getSkus();
                    if (skus.size() > 0) {
                        arrayList.add(new com.mojang.minecraftpe.store.Purchase(skus.get(0), createReceipt(next), next.getPurchaseState() == 1));
                    }
                } else {
//                    String decrypt2 = StringFog.decrypt("PKTq9EeQJmsassf6R5kfaRyC6ONH\n", "e8uFkyv1dgc=\n");
                    Log.v("TAGG", "onQueryPurchasesResponse: num of purchases sent to c++ " + arrayList.size());
                    this.mListener.onQueryPurchasesSuccess((com.mojang.minecraftpe.store.Purchase[]) arrayList.toArray(new com.mojang.minecraftpe.store.Purchase[0]));
                    return;
                }
            }
        } else {
            this.mListener.onQueryPurchasesFail();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {
        int responseCode = billingResult.getResponseCode();
        Log.v("TAGG", "onPurchasesUpdated: BillingResponse " + responseCode + " " + billingResult.getDebugMessage());
        if (responseCode == 0) {
            for (Purchase purchase : list) {
                int purchaseState = purchase.getPurchaseState();
                if (purchaseState != 1) {
                    Log.v("TAGG", "onPurchasesUpdated: PurchaseState " + purchaseState);
                    this.mListener.onPurchasePending(purchase.getSkus().get(0));
                } else if (Security.verifyPurchase(this.mSignatureBase64, purchase.getOriginalJson(), purchase.getSignature())) {
                    this.mListener.onPurchaseSuccessful(purchase.getSkus().get(0), createReceipt(purchase));
                } else {
                    this.mListener.onPurchaseFailed(purchase.getSkus().get(0));
                }
            }
        } else if (responseCode == 1) {
            if (list != null) {
                for (Purchase purchase2 : list) {
                    this.mListener.onPurchaseCanceled(purchase2.getSkus().get(0));
                }
                return;
            }
            this.mListener.onPurchaseCanceled(this.mSkuInProgress);
        } else if (list != null) {
            for (Purchase purchase3 : list) {
                this.mListener.onPurchaseFailed(purchase3.getSkus().get(0));
            }
        } else {
            this.mListener.onPurchaseFailed(this.mSkuInProgress);
        }
    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {
        int responseCode = billingResult.getResponseCode();
//        String decrypt = StringFog.decrypt("TamBkeuhLZprv6yf66gUmG2Pg4br\n", "Csbu9ofEffY=\n");
        Log.v("TAGG", "onSkuDetailsResponse: BillingResponse " + responseCode + " " + billingResult.getDebugMessage());
        if (responseCode == 0) {
            ArrayList arrayList = new ArrayList();
            for (SkuDetails skuDetails : list) {
                Product _prd = new Product(skuDetails.getSku(), skuDetails.getPrice(), skuDetails.getPriceCurrencyCode(), skuDetails.getOriginalPrice());
                arrayList.add(_prd);
                this.mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
            }
            this.mListener.onQueryProductsSuccess((Product[]) arrayList.toArray(new Product[0]));
            return;
        }
        this.mListener.onQueryProductsFail();
    }

    private String createReceipt(Purchase purchase) {
        JSONObject jSONObject = new JSONObject();
        if (purchase != null) {
            try {
                Map<String, SkuDetails> map = this.mSkuDetailsMap;
                if (map != null) {
                    String type = map.get(purchase.getSkus().get(0)).getType();
                    JSONObject jSONObject2 = new JSONObject(purchase.getOriginalJson());
                    String str = new String(Base64.decode(jSONObject2.getString("obfuscatedAccountId"), 0));
                    if (type.equals("subs")) {
                        String str2 = new String(Base64.decode(jSONObject2.getString("obfuscatedProfileId"), 0));
                        if (str2.equals(mNewSubscriptionTag)) {
                            str2 = "";
                        }
                        JSONObject jSONObject3 = new JSONObject(str);
                        jSONObject3.put("subscription_id", str2);
                        jSONObject3.put("world_name", this.mWorldName);
                        str = jSONObject3.toString();
                    }
                    jSONObject2.put("developerPayload", str);
                    Iterator<String> keys = jSONObject2.keys();
                    while (keys.hasNext()) {
                        String next = keys.next();
                        if (next.equals("obfuscatedAccountId") || next.equals("obfuscatedProfileId")) {
                            keys.remove();
                        }
                    }
                    jSONObject.put("itemtype", type);
                    jSONObject.put("originaljson", jSONObject2.toString());
                    jSONObject.put("signature", purchase.getSignature());
                    Log.v("TAGG", jSONObject.toString());
                } else {
                    Log.v("TAGG", "skuDetails map was null");
                }
            } catch (JSONException e) {
                Log.e("TAGG", e.getLocalizedMessage());
                return null;
            }
        } else {
            Log.v("TAGG", "Null purchase in createReceipt");
        }
        return jSONObject.toString();
    }

    private Purchase parseReceipt(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            return new Purchase(jSONObject.getString("originaljson"), jSONObject.getString("signature"));
        } catch (JSONException e) {
            Log.e("TAGG", e.getLocalizedMessage());
            return null;
        }
    }
}

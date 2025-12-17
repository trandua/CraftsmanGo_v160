package com.mojang.minecraftpe.store.amazonappstore;

import android.content.Context;
import android.util.Log;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;
//import com.craftsman.go.StringFog;
import com.mojang.minecraftpe.store.ExtraLicenseResponseData;
import com.mojang.minecraftpe.store.Purchase;
import com.mojang.minecraftpe.store.Store;
import com.mojang.minecraftpe.store.StoreListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class AmazonAppStore implements Store {
    private boolean mForFireTV;
    public StoreListener mListener;
    private PurchasingListener mPurchasingListener;
    public Map<RequestId, String> mProductIdRequestMapping = new HashMap();
    private final String subscriptionKey = ".subscription";
    public Currency userCurrency = null;
    public Locale userLocale = null;

    @Override // com.mojang.minecraftpe.store.Store
    public void destructor() {
    }

    @Override // com.mojang.minecraftpe.store.Store
    public boolean hasVerifiedLicense() {
        return false;
    }

    @Override // com.mojang.minecraftpe.store.Store
    public void purchaseGame() {
    }

    @Override // com.mojang.minecraftpe.store.Store
    public boolean receivedLicenseResponse() {
        return false;
    }

    @Override // com.mojang.minecraftpe.store.Store
    public String getStoreId() {
        return "android.amazonappstore";
    }

    public AmazonAppStore(Context context, StoreListener storeListener, boolean z) {
        PurchasingListener purchasingListener = new PurchasingListener() { // from class: com.mojang.minecraftpe.store.amazonappstore.AmazonAppStore.1
            @Override // com.amazon.device.iap.PurchasingListener
            public void onUserDataResponse(UserDataResponse userDataResponse) {
                if (userDataResponse == null || userDataResponse.getUserData() == null) {
                    return;
                }
                AmazonAppStore.this.userLocale = new Locale(Locale.getDefault().getLanguage(), userDataResponse.getUserData().getMarketplace());
                AmazonAppStore amazonAppStore = AmazonAppStore.this;
                amazonAppStore.userCurrency = Currency.getInstance(amazonAppStore.userLocale);
            }

            @Override // com.amazon.device.iap.PurchasingListener
            public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse) {
                if (purchaseUpdatesResponse.getRequestStatus() != PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL) {
                    AmazonAppStore.this.mListener.onQueryPurchasesFail();
                    return;
                }
                ArrayList arrayList = new ArrayList();
                String userId = purchaseUpdatesResponse.getUserData().getUserId();
                for (Receipt receipt : purchaseUpdatesResponse.getReceipts()) {
                    arrayList.add(new Purchase(receipt.getSku(), AmazonAppStore.this.createReceipt(userId, receipt.getReceiptId()), !receipt.isCanceled()));
                }
                AmazonAppStore.this.mListener.onQueryPurchasesSuccess((Purchase[]) arrayList.toArray(new Purchase[0]));
            }

            @Override // com.amazon.device.iap.PurchasingListener
            public void onPurchaseResponse(PurchaseResponse purchaseResponse) {
                String remove = AmazonAppStore.this.mProductIdRequestMapping.remove(purchaseResponse.getRequestId());
                if (purchaseResponse.getRequestStatus() == PurchaseResponse.RequestStatus.SUCCESSFUL) {
                    AmazonAppStore.this.mListener.onPurchaseSuccessful(remove, AmazonAppStore.this.createReceipt(purchaseResponse));
                } else {
                    AmazonAppStore.this.mListener.onPurchaseFailed(remove);
                }
            }

            @Override // com.amazon.device.iap.PurchasingListener
            public void onProductDataResponse(ProductDataResponse productDataResponse) {
                if (productDataResponse.getRequestStatus() != ProductDataResponse.RequestStatus.SUCCESSFUL) {
                    AmazonAppStore.this.mListener.onQueryProductsFail();
                    return;
                }
                ArrayList arrayList = new ArrayList();
                Set<String> unavailableSkus = productDataResponse.getUnavailableSkus();
                Map<String, Product> productData = productDataResponse.getProductData();
                for (String str : productData.keySet()) {
                    if (!unavailableSkus.contains(str)) {
                        Product product = productData.get(str);
                        String str2 = "";
                        String replace = product.getSku() != null ? product.getSku().replace(".child", "") : "";
                        String price = product.getPrice() != null ? product.getPrice() : "";
                        Locale locale = AmazonAppStore.this.userLocale;
                        String decrypt = "0";
                        if (locale != null && AmazonAppStore.this.userCurrency != null) {
                            try {
                                str2 = AmazonAppStore.this.userCurrency.getCurrencyCode();
                                decrypt = NumberFormat.getCurrencyInstance(AmazonAppStore.this.userLocale).parse(price).toString();
                            } catch (Exception e) {
//                                Log.i(StringFog.decrypt("9bYOth5O0L3EiBujA0U=\n", "tNtvzHEgkc0=\n"), e.getMessage());
                            }
                        }
//                        String decrypt2 = StringFog.decrypt("uLNtsKgIcmOJjXiltQM=\n", "+d4MysdmMxM=\n");
//                        Log.i(decrypt2, StringFog.decrypt("/jk58TPDfMaheyzxNcV2xLZnOOs4wmC2snAspCXacM0=\n", "0xRIhFaxBZY=\n") + replace + StringFog.decrypt("QzbfGKyyEls=\n", "HhavasXRdwA=\n") + price + StringFog.decrypt("h7toSQgV4Ye54khTHgLf\n", "2psLPHpnhOk=\n") + str2 + StringFog.decrypt("+IJdqKWeEPTE1lyjp6o=\n", "paIoxsPxYpk=\n") + decrypt + StringFog.decrypt("bw==\n", "MtLWjinkYa4=\n"));
                        arrayList.add(new com.mojang.minecraftpe.store.Product(replace, price, str2, decrypt));
                    }
                }
                AmazonAppStore.this.mListener.onQueryProductsSuccess((com.mojang.minecraftpe.store.Product[]) arrayList.toArray(new com.mojang.minecraftpe.store.Product[0]));
            }
        };
        this.mPurchasingListener = purchasingListener;
        this.mListener = storeListener;
        this.mForFireTV = z;
        PurchasingService.registerListener(context, purchasingListener);
        storeListener.onStoreInitialized(true);
    }

    @Override // com.mojang.minecraftpe.store.Store
    public String getProductSkuPrefix() {
        return this.mForFireTV ? "firetv." : "";
    }

    @Override // com.mojang.minecraftpe.store.Store
    public String getRealmsSkuPrefix() {
        return this.mForFireTV ? "firetv." : "";
    }

    @Override // com.mojang.minecraftpe.store.Store
    public ExtraLicenseResponseData getExtraLicenseData() {
        return new ExtraLicenseResponseData(0L, 0L, 0L);
    }

    @Override // com.mojang.minecraftpe.store.Store
    public void queryProducts(String[] strArr) {
        String[] strArr2 = new String[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i].indexOf(this.subscriptionKey) != -1) {
                strArr2[i] = strArr[i] + ".child";
            } else {
                strArr2[i] = strArr[i];
            }
        }
        PurchasingService.getUserData();
        PurchasingService.getProductData(new HashSet(Arrays.asList(strArr2)));
    }

    @Override // com.mojang.minecraftpe.store.Store
    public void purchase(String str, boolean z, String str2) {
        this.mProductIdRequestMapping.put(PurchasingService.purchase(str), str);
    }

    @Override // com.mojang.minecraftpe.store.Store
    public void acknowledgePurchase(String str, String str2) {
        try {
            PurchasingService.notifyFulfillment(new JSONObject(str).getString("receiptId"), FulfillmentResult.FULFILLED);
        } catch (JSONException unused) {
        }
    }

    @Override // com.mojang.minecraftpe.store.Store
    public void queryPurchases() {
        PurchasingService.getPurchaseUpdates(true);
    }

    public String createReceipt(String str, String str2) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("userId", str);
            jSONObject.put("receiptId", str2);
            return jSONObject.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String createReceipt(PurchaseResponse purchaseResponse) {
        return createReceipt(purchaseResponse.getUserData().getUserId(), purchaseResponse.getReceipt().getReceiptId());
    }
}

package com.mojang.minecraftpe.store;

/* loaded from: classes3.dex */
public class NativeStoreListener implements StoreListener {
    long mStoreListener;

    public native void onPurchaseCanceled(long j, String str);

    public native void onPurchaseFailed(long j, String str);

    public native void onPurchasePending(long j, String str);

    public native void onPurchaseSuccessful(long j, String str, String str2);

    public native void onQueryProductsFail(long j);

    public native void onQueryProductsSuccess(long j, Product[] productArr);

    public native void onQueryPurchasesFail(long j);

    public native void onQueryPurchasesSuccess(long j, Purchase[] purchaseArr);

    public native void onStoreInitialized(long j, boolean z);

    public NativeStoreListener(long j) {
        this.mStoreListener = j;
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onStoreInitialized(boolean z) {
        onStoreInitialized(this.mStoreListener, z);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onQueryProductsSuccess(Product[] productArr) {
        onQueryProductsSuccess(this.mStoreListener, productArr);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onQueryProductsFail() {
        onQueryProductsFail(this.mStoreListener);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onPurchaseSuccessful(String str, String str2) {
        onPurchaseSuccessful(this.mStoreListener, str, str2);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onPurchaseCanceled(String str) {
        onPurchaseCanceled(this.mStoreListener, str);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onPurchaseFailed(String str) {
        onPurchaseFailed(this.mStoreListener, str);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onPurchasePending(String str) {
        onPurchasePending(this.mStoreListener, str);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onQueryPurchasesSuccess(Purchase[] purchaseArr) {
        onQueryPurchasesSuccess(this.mStoreListener, purchaseArr);
    }

    @Override // com.mojang.minecraftpe.store.StoreListener
    public void onQueryPurchasesFail() {
        onQueryPurchasesFail(this.mStoreListener);
    }
}

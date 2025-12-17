package com.mojang.minecraftpe.store;

/* loaded from: classes3.dex */
public class Purchase {
    public String mProductId;
    public boolean mPurchaseActive;
    public String mReceipt;

    public Purchase(String str, String str2, boolean z) {
        this.mProductId = str;
        this.mReceipt = str2;
        this.mPurchaseActive = z;
    }
}

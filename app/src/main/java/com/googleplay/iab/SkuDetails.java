package com.googleplay.iab;

import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SkuDetails {
    private final String mCurrencyCode;
    private final String mDescription;
    private final String mItemType;
    private final String mJson;
    private final String mPrice;
    private final long mPriceAmountMicros;
    private final String mPriceCurrencyCode;
    private final String mSku;
    private final String mTitle;
    private final String mType;
    private final String mUnformattedPrice;

    public SkuDetails(String str) throws JSONException {
        this("inapp", str);
    }

    public SkuDetails(String str, String str2) throws JSONException {
        this.mItemType = str;
        this.mJson = str2;
        JSONObject jSONObject = new JSONObject(str2);
        this.mSku = jSONObject.optString("productId");
        this.mType = jSONObject.optString("type");
        this.mPrice = jSONObject.optString("price");
        this.mPriceAmountMicros = jSONObject.optLong("price_amount_micros");
        this.mPriceCurrencyCode = jSONObject.optString("price_currency_code");
        this.mTitle = jSONObject.optString("title");
        this.mDescription = jSONObject.optString("description");
        this.mCurrencyCode = jSONObject.optString("price_currency_code");
        StringBuilder sb = new StringBuilder();
        long optLong = jSONObject.optLong("price_amount_micros");
        if (optLong != 0) {
            long j = optLong / 1000000;
            long j2 = optLong - (1000000 * j);
            sb.append(Long.toString(j));
            if (j2 >= 0) {
                sb.append(".");
                sb.append(Long.toString(j2));
            }
        }
        this.mUnformattedPrice = sb.toString();
    }

    public String getCurrencyCode() {
        return this.mCurrencyCode;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public long getPriceAmountMicros() {
        return this.mPriceAmountMicros;
    }

    public String getPriceCurrencyCode() {
        return this.mPriceCurrencyCode;
    }

    public String getSku() {
        return this.mSku;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getType() {
        return this.mType;
    }

    public String getUnformattedPrice() {
        return this.mUnformattedPrice;
    }

    public String toString() {
        return "SkuDetails:" + this.mJson;
    }
}

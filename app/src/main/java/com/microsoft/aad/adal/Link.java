package com.microsoft.aad.adal;

import com.google.gson.annotations.SerializedName;

/* loaded from: classes3.dex */
final class Link {
    @SerializedName("href")
    private String mHref;
    @SerializedName("rel")
    private String mRel;

    Link() {
    }

    public String getHref() {
        return this.mHref;
    }

    public String getRel() {
        return this.mRel;
    }

    public void setHref(String str) {
        this.mHref = str;
    }

    public void setRel(String str) {
        this.mRel = str;
    }
}

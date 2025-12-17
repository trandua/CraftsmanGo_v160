package com.microsoft.aad.adal;

import com.google.gson.annotations.SerializedName;

/* loaded from: classes3.dex */
final class IdentityProviderService {
    @SerializedName("PassiveAuthEndpoint")
    private String mPassiveAuthEndpoint;

    IdentityProviderService() {
    }

    public String getPassiveAuthEndpoint() {
        return this.mPassiveAuthEndpoint;
    }

    public void setPassiveAuthEndpoint(String str) {
        this.mPassiveAuthEndpoint = str;
    }
}

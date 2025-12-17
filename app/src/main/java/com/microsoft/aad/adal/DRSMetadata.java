package com.microsoft.aad.adal;

import com.google.gson.annotations.SerializedName;

/* loaded from: classes3.dex */
final class DRSMetadata {
    @SerializedName("IdentityProviderService")
    private IdentityProviderService mIdentityProviderService;

    DRSMetadata() {
    }

    public IdentityProviderService getIdentityProviderService() {
        return this.mIdentityProviderService;
    }

    public void setIdentityProviderService(IdentityProviderService identityProviderService) {
        this.mIdentityProviderService = identityProviderService;
    }
}

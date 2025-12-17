package com.microsoft.aad.adal;

import java.net.URL;

/* loaded from: classes3.dex */
final class WebFingerMetadataRequestParameters {
    private final URL mDomain;
    private final DRSMetadata mMetadata;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebFingerMetadataRequestParameters(URL url, DRSMetadata dRSMetadata) {
        this.mDomain = url;
        this.mMetadata = dRSMetadata;
    }

    public URL getDomain() {
        return this.mDomain;
    }

    public DRSMetadata getDrsMetadata() {
        return this.mMetadata;
    }
}

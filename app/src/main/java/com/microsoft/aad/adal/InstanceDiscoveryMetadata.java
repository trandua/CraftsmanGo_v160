package com.microsoft.aad.adal;

import java.util.ArrayList;
import java.util.List;

/* loaded from: classes3.dex */
final class InstanceDiscoveryMetadata {
    private final List<String> mAliases;
    private final boolean mIsValidated;
    private final String mPreferredCache;
    private final String mPreferredNetwork;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InstanceDiscoveryMetadata(String str, String str2) {
        this.mAliases = new ArrayList();
        this.mPreferredNetwork = str;
        this.mPreferredCache = str2;
        this.mIsValidated = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InstanceDiscoveryMetadata(String str, String str2, List<String> list) {
        ArrayList arrayList = new ArrayList();
        this.mAliases = arrayList;
        this.mPreferredNetwork = str;
        this.mPreferredCache = str2;
        arrayList.addAll(list);
        this.mIsValidated = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InstanceDiscoveryMetadata(boolean z) {
        this.mAliases = new ArrayList();
        this.mIsValidated = z;
        this.mPreferredNetwork = null;
        this.mPreferredCache = null;
    }

    public List<String> getAliases() {
        return this.mAliases;
    }

    public String getPreferredCache() {
        return this.mPreferredCache;
    }

    public String getPreferredNetwork() {
        return this.mPreferredNetwork;
    }

    public boolean isValidated() {
        return this.mIsValidated;
    }
}

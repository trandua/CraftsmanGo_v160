package com.googleplay.licensing;

/* loaded from: classes2.dex */
public class StrictPolicy implements Policy {
    private int mLastResponse = Policy.RETRY;

    @Override // com.googleplay.licensing.Policy
    public boolean allowAccess() {
        return true;
    }

    @Override // com.googleplay.licensing.Policy
    public void processServerResponse(int i, ResponseData responseData) {
        this.mLastResponse = i;
    }
}

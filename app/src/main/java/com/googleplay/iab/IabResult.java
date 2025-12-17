package com.googleplay.iab;

/* loaded from: classes2.dex */
public class IabResult {
    String mMessage;
    int mResponse;

    public IabResult(int i, String str) {
        String responseDesc;
        this.mResponse = i;
        if (str == null || str.trim().length() == 0) {
            responseDesc = IabHelper.getResponseDesc(i);
        } else {
            responseDesc = str + " (response: " + IabHelper.getResponseDesc(i) + ")";
        }
        this.mMessage = responseDesc;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public int getResponse() {
        return this.mResponse;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public boolean isSuccess() {
        return this.mResponse == 0;
    }

    public String toString() {
        return "IabResult: " + getMessage();
    }
}

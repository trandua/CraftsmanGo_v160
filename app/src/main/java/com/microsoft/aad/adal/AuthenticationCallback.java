package com.microsoft.aad.adal;

/* loaded from: classes3.dex */
public interface AuthenticationCallback<T> {
    void onError(Exception exc);

    void onSuccess(T t);
}
